import org.apache.log4j.{Level, Logger}
import org.apache.spark.api.java.JavaSparkContext
import org.apache.spark.mllib.clustering.{KMeans, KMeansModel}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.regression.LinearRegressionModel
import org.apache.spark.mllib.regression.LinearRegressionWithSGD

object LinearRegressionwithSGD {

  def main(args: Array[String]): Unit ={


    System.setProperty("hadoop.home.dir","C:\\Users\\mattv\\Desktop\\UMKC\\Big Data\\hadoop");

    val sparkConf = new SparkConf().setAppName("SparkWordCount").setMaster("local[*]")
    sparkConf.set("spark.driver.allowMultipleContexts", "true");

    val sc=new SparkContext(sparkConf)

    // Turn off Info Logger for Consolexxx
    Logger.getLogger("org").setLevel(Level.OFF);
    Logger.getLogger("akka").setLevel(Level.OFF);

    // Load and parse the data
    //Load the Chimp parameters of Verticality and Speed
    val data = sc.textFile("data\\chimp.data")
    val parsedData = data.map { line =>
      val parts = line.split(',')
      LabeledPoint(parts(0).toDouble, Vectors.dense(parts(1).split(' ').map(_.toDouble)))
    }.cache()

    parsedData.take(1).foreach(f=>println(f))

    // Split data into training (95%) and test (5%).
    val Array(training, test) = parsedData.randomSplit(Array(0.95, 0.05))

    // Building the model
    val numIterations = 100
    val stepSize = 0.00000001
    val model = LinearRegressionWithSGD.train(training, numIterations, stepSize)

    // Evaluate model on training examples and compute training error
    val valuesAndPreds = training.map { point =>
      val prediction = model.predict(point.features)
      (point.label, prediction)
    }
    val MSE = valuesAndPreds.map{ case(v, p) => math.pow((v - p), 2) }.mean()
    println("training Mean Squared Error = " + MSE)

    // Evaluate model on test examples and compute training error
    val valuesAndPreds2 = test.map { point =>
      val prediction = model.predict(point.features)
      (point.label, prediction)
    }
    val MSE2 = valuesAndPreds2.map{ case(v, p) => math.pow((v - p), 2) }.mean()
    println("test Mean Squared Error = " + MSE2)

    // Save and load model
    model.save(sc, "data\\LinearRegressionWithSGDModel")
    val linearModel = LinearRegressionModel.load(sc, "data\\LinearRegressionWithSGDModel")
    sc.stop();
    kMeansClustering.main(null);
  }
}
object kMeansClustering {

  def main(args: Array[String]): Unit = {

    System.setProperty("hadoop.home.dir","C:\\Users\\Manikanta\\Documents\\UMKC Subjects\\PB\\hadoopforspark");

    val sparkConf = new SparkConf().setAppName("SparkWordCount").setMaster("local[*]")

    val sc=new SparkContext(sparkConf)

    // Turn off Info Logger for Consolexxx
    Logger.getLogger("org").setLevel(Level.OFF);
    Logger.getLogger("akka").setLevel(Level.OFF);
    // Load and parse the data
    //Load the Chimp's activities of sleeping and running
    val data = sc.textFile("data/chimp_kmeans_data")
    val parsedData = data.map(s => Vectors.dense(s.split(' ').map(_.toDouble))).cache()

    parsedData.foreach(f=>println(f))

    // Cluster the data into two classes using KMeans
    val numClusters = 2
    val numIterations = 20
    val clusters = KMeans.train(parsedData, numClusters, numIterations)

    // Evaluate clustering by computing Within Set Sum of Squared Errors
    val WSSSE = clusters.computeCost(parsedData)
    println("Within Set Sum of Squared Errors = " + WSSSE)

    //Look at how the clusters are in training data by making predictions
    println("Clustering on training data: ")
    clusters.predict(parsedData).zip(parsedData).foreach(f=>println(f._2,f._1))

    // Save and load model
    clusters.save(sc, "data/KMeansModel")
    val sameModel = KMeansModel.load(sc, "data/KMeansModel")
  }
}

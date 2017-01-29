import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by Matthew_2 on 1/28/2017.
  * This program will read words from an input file and output their corresponding length
  */
object lab2 {

  def main(args: Array[String]): Unit = {


    System.setProperty("hadoop.home.dir", "C:\\Users\\Matthew_2\\Desktop\\UMKC\\Big Data\\hadoop");

    val sparkConf = new SparkConf().setAppName("Lab2").setMaster("local[*]")

    val sc = new SparkContext(sparkConf)

    val input=sc.textFile("input")
    val splitLines = input.flatMap(line=>{line.split(" ")}).map(word=>(word,word.length)).cache()
    val wordLength = splitLines.reduceByKey(_+_, 1).map(item => item.swap).sortByKey(false, 1).map(item => item.swap)
    wordLength.saveAsTextFile("output")
    val output = wordLength.collect()
    var s:String="Length of Words \n"
    wordLength.foreach{case(word,length)=>{

      s+=word+" : "+length+"\n"
    }}
  }
}

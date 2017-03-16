import tensorflow as tf
tf.logging.set_verbosity(tf.logging.DEBUG)
sess = tf.Session()

# import mnist data
from tensorflow.examples.tutorials.mnist import input_data
mnist = input_data.read_data_sets("MNIST_data/", one_hot=True)

# restore the saved model
new_saver = tf.train.import_meta_graph('data/mnist_model/00000001/export.meta')
new_saver.restore(sess, 'data/mnist_model/00000001/export')

# print to see the restored variables
for v in tf.get_collection('variables'):
    print(v.name)
print(sess.run(tf.global_variables()))

# get saved weights
W = tf.get_collection('variables')[0]
b = tf.get_collection('variables')[1]

# placeholders for test images and labels
x = tf.placeholder(tf.float32, [None, 784],name='x')
y_ = tf.placeholder(tf.float32, [None, 10],name='y_')

# predict equation
y = tf.nn.softmax(tf.matmul(x, W) + b,name='y')

# compare predicted label and actual label
correct_prediction = tf.equal(tf.argmax(y,1), tf.argmax(y_,1))

# accuracy op
accuracy = tf.reduce_mean(tf.cast(correct_prediction, tf.float32))

accu=sess.run(accuracy, feed_dict={x: mnist.test.images, y_: mnist.test.labels})
print(accu)

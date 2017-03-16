

import tensorflow as tf
from tensorflow.contrib.session_bundle import exporter

# Import MNIST data
from tensorflow.examples.tutorials.mnist import input_data
mnist = input_data.read_data_sets("MNIST_data/", one_hot=True)


sess = tf.Session()
tf.logging.set_verbosity(tf.logging.INFO)

x = tf.placeholder(tf.float32, [None, 784],name='x')
W = tf.Variable(tf.zeros([784, 10]),name='W')
b = tf.Variable(tf.zeros([10]),name='b')

y = tf.nn.softmax(tf.matmul(x, W) + b,name='y')
y_ = tf.placeholder(tf.float32, [None, 10],name='y_')
tf.add_to_collection('variables',W)
tf.add_to_collection('variables',b)

cross_entropy = tf.reduce_mean(tf.nn.softmax_cross_entropy_with_logits(logits=y, labels=y_))

train_step = tf.train.GradientDescentOptimizer(0.5).minimize(cross_entropy)

# save summaries for visualization
tf.summary.histogram('weights', W)
tf.summary.histogram('max_weight', tf.reduce_max(W))
tf.summary.histogram('bias', b)
tf.summary.scalar('cross_entropy', cross_entropy)
tf.summary.histogram('cross_hist', cross_entropy)

# merge all summaries into one op
merged=tf.summary.merge_all()

trainwriter=tf.summary.FileWriter('data/mnist_model'+'/logs/train',sess.graph)

init = tf.global_variables_initializer()
sess.run(init)

for i in range(1000):
    batch_xs, batch_ys = mnist.train.next_batch(100)
    summary, _ = sess.run([merged, train_step], feed_dict={x: batch_xs, y_: batch_ys})
    trainwriter.add_summary(summary, i)

# model export path
export_path = 'data/mnist_model'
print('Exporting trained model to', export_path)

#
saver = tf.train.Saver(sharded=True)
model_exporter = exporter.Exporter(saver)
model_exporter.init(
    sess.graph.as_graph_def(),
    named_graph_signatures={
        'inputs': exporter.generic_signature({'images': x}),
        'outputs': exporter.generic_signature({'scores': y})})

model_exporter.export(export_path, tf.constant(1), sess)

"""
can also save the model using saver as follows
saver.save(sess, '/home/manikanta/tensorflow/mnist_model')
"""
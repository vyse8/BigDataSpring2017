import tensorflow as tf, sys
from flask import Flask, jsonify, render_template, request
from flask_cors import CORS, cross_origin
import base64
import tensorflow as tf, sys
import urllib, cStringIO
import os
import wget

app = Flask(__name__)

# def download_image(img_str, url):
#     # with open(img_str, "wb") as fh:
#     #     fh.write(urllib.urlopen(url).read())
#     f = open(img_str, 'wb')
#     dat = urllib.urlopen(url).read()
#     print "dat",dat
#     f.write(str(dat))
#     print("written ", img_str, " ", url)
#     f.close()

def remove_images():
    import os
    path = os.path.abspath('static/tmp')
    for file_name in os.listdir('static/tmp'):
        os.remove(path+file_name)

@app.route('/', methods=['POST'])
@cross_origin()
def method():
    print "request received"
    data = request.get_json()
    print "data",data
    print "json_data",data['imageBase64']
    image_list_str=data['imageBase64']
    print "image_list",image_list_str
    # print "urls",data.getlist('imageBase64')
    # print "data.values",data.values['imageBase64']
    # image_list=['http://i63.tinypic.com/10hq0wx.jpg',
    #             'http://i66.tinypic.com/2usx6xw.jpg',
    #             'http://i67.tinypic.com/1zmdoph.jpg',
    #             'http://i63.tinypic.com/5xju40.jpg',
    #             'http://i64.tinypic.com/2llf09e.jpg',
    #             'http://i66.tinypic.com/invhb7.jpg',
    #             'http://i64.tinypic.com/v3p1dl.jpg',
    #             'http://i64.tinypic.com/1fg5yf.jpg',
    #             'http://i64.tinypic.com/v2ur6w.jpg',
    #             'http://i66.tinypic.com/2gw5091.jpg',
    #             'http://i65.tinypic.com/5bpf6a.jpg',
    #             'http://i64.tinypic.com/rsud7o.jpg',
    #             'http://i63.tinypic.com/2cpe149.jpg',
    #             'http://i64.tinypic.com/20z33vq.jpg',
    #             'http://i65.tinypic.com/11kb76g.jpg'
    #             ]
    # file = cStringIO.StringIO(urllib2.urlopen('http://localhost:3939/static/Image/Image1.jpg').read())
    # image_list=[]
    # image_list.append(image_list_str)
    file_name = []
    for counter, image_url in enumerate(image_list_str):
        import os
        print image_url
        path=os.path.abspath('./static/tmp/')
        print path
        # file_name=str(path+"/"+str(counter)+'.jpg')
        # file_name="0.jpg"
        # print(file_name)
        # download_image(file_name, image_url)

        file_name.append(wget.download(image_url))
        print(file_name)

        # with open(file_name, "w") as fh:
        #     print("Inside open", fh)
        #     str(urllib.urlretrieve(image_url))
        #     print("Closing the file")
        #     fh.close()
        #     print("End of open", fh)

        # fh = open(file_name, "r")
        # print("trying to read the file just written")
        # fh.read()
        # print("Just outside open", fh)

    # f.write(urllib.urlopen('http://i63.tinypic.com/10hq0wx.jpg').read())
    # f.close()
    print("loop complete")
    tf_predictions = []
    # Loads label file, strips off carriage return
    label_lines = [line.rstrip() for line
                   in tf.gfile.GFile("data/output_labels.txt")]

    # Unpersists graph from file
    with tf.gfile.FastGFile("data/output_graph.pb", 'rb') as f:
        graph_def = tf.GraphDef()
        graph_def.ParseFromString(f.read())
        _ = tf.import_graph_def(graph_def, name='')

    for file in file_name:
        # Read in the image_data
        print file
        image_data = tf.gfile.FastGFile(file, 'rb').read()

        with tf.Session() as sess:
            # Feed the image_data as input to the graph and get first prediction
            softmax_tensor = sess.graph.get_tensor_by_name('final_result:0')

            predictions = sess.run(softmax_tensor, \
                               {'DecodeJpeg/contents:0': image_data})

            # Sort to show labels of first prediction in order of confidence
            top_k = predictions[0].argsort()[-len(predictions[0]):][::-1]
            tf_predictions.append(label_lines[top_k[0]])

    print tf_predictions

    # remove_images()
    return repr(tf_predictions)
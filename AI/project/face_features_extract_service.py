"""Performs face alignment and features extract."""

from __future__ import absolute_import
from __future__ import division
from __future__ import print_function

from scipy import misc
import tensorflow as tf
import numpy as np
import sys
sys.path.append('./facenet-master/src')
import os
import argparse
import facenet
import align.detect_face

from flask import Flask, request

app = Flask(__name__)

@app.route('/faceFeaturesExtract', methods=['POST', 'GET'])
def extract():
    if request.method == 'GET':
        img = str(request.values.get('img'))
    elif request.method == 'POST':
        img = str(request.form.get('img'))
    image = load_and_align_data(img)
    result = ''
    if len(image) is not 0 and image.any():
        # Run forward pass to calculate embeddings
        feed_dict = { images_placeholder: image, phase_train_placeholder:False }
        emb = sess.run(embeddings, feed_dict=feed_dict)
        for item in emb[0]:
            result += (str(item) + ',')
        return result
    else:
        return result
            
            
def load_and_align_data(image_path, image_size=160, margin=40, gpu_memory_fraction=0.2):

    minsize = 20 # minimum size of face
    threshold = [ 0.6, 0.7, 0.7 ]  # three steps's threshold
    factor = 0.7 # scale factor
    
    print('Creating networks and loading parameters')
    with tf.Graph().as_default():
        gpu_options = tf.GPUOptions(per_process_gpu_memory_fraction=gpu_memory_fraction)
        sess = tf.Session(config=tf.ConfigProto(gpu_options=gpu_options, log_device_placement=False))
        with sess.as_default():
            pnet, rnet, onet = align.detect_face.create_mtcnn(sess, None)
  
    image = image_path
    img_list = []
    img = misc.imread(image, mode='RGB')
    img_size = np.asarray(img.shape)[0:2]
    bounding_boxes, _ = align.detect_face.detect_face(img, minsize, pnet, rnet, onet, threshold, factor)
    if len(bounding_boxes) < 1:
        return None
    det = np.squeeze(bounding_boxes[0,0:4])
    bb = np.zeros(4, dtype=np.int32)
    bb[0] = np.maximum(det[0]-margin/2, 0)
    bb[1] = np.maximum(det[1]-margin/2, 0)
    bb[2] = np.minimum(det[2]+margin/2, img_size[1])
    bb[3] = np.minimum(det[3]+margin/2, img_size[0])
    cropped = img[bb[1]:bb[3],bb[0]:bb[2],:]
    aligned = misc.imresize(cropped, (image_size, image_size), interp='bilinear')
    prewhitened = facenet.prewhiten(aligned)
    img_list.append(prewhitened)
    images = np.stack(img_list)

    return images

if __name__ == '__main__':
    model = './20170512-110547/'

    with tf.Graph().as_default():

        with tf.Session() as sess:
      
            # Load the model
            facenet.load_model(model)
    
            # Get input and output tensors
            images_placeholder = tf.get_default_graph().get_tensor_by_name("input:0")
            embeddings = tf.get_default_graph().get_tensor_by_name("embeddings:0")
            phase_train_placeholder = tf.get_default_graph().get_tensor_by_name("phase_train:0")    
            
            app.run(host='127.0.0.1', port=8888)

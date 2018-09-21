#coding=utf-8
from datetime import timedelta
from flask import Flask,request,session, app
import os
import sys
import ctypes
import time
import numpy as np
from detectBlinks import *

from PIL import Image
import json
import urllib2
import base64

EXTS = 'jpg', 'jpeg', 'JPG', 'JPEG', 'gif', 'GIF', 'png', 'PNG'
UPLOAD_FOLDER = './upload'
FULL_VIEW_UPLOAD_FOLDER = './full_view_upload'
FACE_FEATURE_CHECK_RUL = 'http://127.0.0.1:8888/faceFeaturesExtract'

def avhash(im):
    im = im.resize((128, 128), Image.ANTIALIAS).convert('L')
    avg = reduce(lambda x, y: x + y, im.getdata()) / 16384.
    return reduce(lambda x, (y, z): x | (z << y),
                  enumerate(map(lambda i: 0 if i < avg else 1, im.getdata())),
                  0)

def hamming(h1, h2):
    h, d = 0, h1 ^ h2
    while d:
        h += 1
        d &= d - 1
    return h


def create_hash(im1, grain):
    if grain is True:
        num = 1
    else:
        num = 128
    if not isinstance(im1, Image.Image):
        im1 = Image.open(im1)
    (im1_width, im1_height) = im1.size
    feature = ''
    for i in range(num):
        im1_part = im1.crop((i*(im1_width / num), i*(im1_height / num), (i+1)*(im1_width / num), (i+1)*(im1_height / num)))
        feature += str(avhash(im1_part))
    return feature

def do_hash(im1, im2_feature, grain):
    if grain is True:
        num = 1
    else:
        num = 128
    if not isinstance(im1, Image.Image):
        im1 = Image.open(im1)
    (im1_width, im1_height) = im1.size
    for i in range(num):
        im1_part = im1.crop((i*(im1_width / num), i*(im1_height / num), (i+1)*(im1_width / num), (i+1)*(im1_height / num)))
    distance = hamming(avhash(im1_part), long(im2_feature))
    print distance
    if distance > 10000:
        return 'No'
    else:
        return 'Yes'

app = Flask(__name__)
app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER
app.config['FULL_VIEW_UPLOAD_FOLDER'] = FULL_VIEW_UPLOAD_FOLDER

@app.route('/hash', methods=['POST', 'GET'])
def function_hash():
    abs_path = os.path.abspath(app.config['UPLOAD_FOLDER'])
    img_save_path = os.path.join(abs_path, str(time.time()) + '.jpg')
    print request.form

    if request.method == 'GET':
        im1 = request.files['im1']
        im2_feature = str(request.values.get('im2feature'))
    else:
        img = request.files['im1']
        if img:
            img.save(img_save_path)
            im2_feature = str(request.form['im2feature'])
        else: 
            data = {'code':'500', 'feature':''}
            result = json.dumps(data)
            return result

    im1 = img_save_path
    new_im1 = im1.replace('.jpg', '_r.jpg')
    grain  = False

    try:
        libb = ctypes.cdll.LoadLibrary('./projection.so')
        res = libb.projection(im1, new_im1)
        if res == -1:
            # exception
            code = '500'
            pass_value = 'wrong image type, please try again'
        else:
            pass_value = do_hash(new_im1, im2_feature, grain)
            print pass_value
            if pass_value == 'Yes':
                code = '200'
            else:
                code = '500'

        data = {'code':code, 'pass':pass_value}
        result = json.dumps(data)
        return result
    except:
        data = {'code':'500', 'pass':'No'}
        result = json.dumps(data)
        return result


@app.route('/save', methods=['POST', 'GET'])
def function_save():
    abs_path = os.path.abspath(app.config['UPLOAD_FOLDER'])
    img_save_path = os.path.join(abs_path, str(time.time()) + '.jpg')

    if request.method == 'GET':
        img = request.files['im1']
        if img:
            img.save(img_save_path)
        else: 
            data = {'code':'500', 'feature':''}
            result = json.dumps(data)
            return result
        grain  = bool(request.values.get('grain'))
    else:
        img = request.files['im1']
        if img:
            img.save(img_save_path)
        else: 
            data = {'code':'500', 'feature':''}
            result = json.dumps(data)
            return result
        grain  = bool(request.form.get('grain'))
    
    im1 = img_save_path
    new_im1 = im1.replace('.', '_r.')

    try:
        libb = ctypes.cdll.LoadLibrary('./projection.so')
        res = libb.projection(im1, new_im1)
        if res == -1:
            # exception
            code = '500'
            feature = 'wrong image type, please try again'
        else:
            code = '200'
            feature = create_hash(new_im1, grain)

        data = {'code':code, 'feature':feature}
        result = json.dumps(data)
        return result
    except:
        data = {'code':'500', 'feature':''}
        result = json.dumps(data)
        return result

@app.route('/upload', methods=['POST', 'GET'])
def up_load():
    abs_path = os.path.abspath(app.config['FULL_VIEW_UPLOAD_FOLDER'])
    if request.method == 'POST':
        name = str(request.form['name']) + '.jpg'
        file = request.files['img']
        if file:
            save_path = os.path.join(abs_path, name)
            file.save(save_path)
            data = {'code':'200', 'filePath':save_path}
            result = json.dumps(data)
            return result
        else:
            data = {'code':'500', 'filePath':'no_path'}
            result = json.dumps(data)
            return result
    else:
        data = {'code':'500', 'filePath':'no_path'}
        result = json.dumps(data)
        return result

@app.route('/livingBodyCheck', methods=['POST', 'GET'])
def living_body_check():
    if request.method == 'GET':
        video_path = request.values.get('filePath')
    else:
        video_path = request.form['filePath']

    blink_count, target_face_path = detect_blinks(video_path)

    if blink_count > 0:
        req = urllib2.Request(FACE_FEATURE_CHECK_RUL + '?img=' + target_face_path)
        response = urllib2.urlopen(req)
        feature = response.read()
        data = {'code':'200', 'feature':feature}
        result = json.dumps(data)
        return result
    else:
        data = {'code':'500', 'feature':'non living body, try again'}
        result = json.dumps(data)
        return result

@app.route('/face', methods=['POST', 'GET'])
def function_face():
    abs_path = os.path.abspath(app.config['UPLOAD_FOLDER'])
    im1 = request.files['im1']
    face_save_path = os.path.join(abs_path, str(time.time()) + '.jpg')
    im1.save(face_save_path)
    req = urllib2.Request(FACE_FEATURE_CHECK_RUL + '?img=' + face_save_path)
    response = urllib2.urlopen(req)
    feature = response.read()
    if feature != '' :
        data = {'code':'200', 'feature':feature}
        result = json.dumps(data)
        return result
    else:
        data = {'code':'500', 'feature':None}
        result = json.dumps(data)
        return result

@app.route('/check', methods=['POST', 'GET'])
def function_check():
    abs_path = os.path.abspath(app.config['UPLOAD_FOLDER'])
    video = request.files['video']
    video_name = str(request.form['videoName'])
    im2_feature = request.form['feature']
    video_path = os.path.join(abs_path, video_name)
    video.save(video_path)

    blink_count, target_face_path = detect_blinks(video_path)

    if blink_count > 0:
        req = urllib2.Request(FACE_FEATURE_CHECK_RUL + '?img=' + target_face_path)
        response = urllib2.urlopen(req)
        im1_feature = response.read()
        print im1_feature

        feature1 = [float(item) for item in im1_feature.split(',')[:-1]]
        feature2 = [float(item) for item in im2_feature.split(',')[:-1]]

        try:
            dist = np.sqrt(np.sum(np.square(np.subtract(feature1, feature2))))

            if dist < 0.6:
                code = '200'
                pass_value = 'Yes'
            else:
                code = '500'
                pass_value = 'No'

            data = {'code':code, 'pass':pass_value}
            result = json.dumps(data)
            return result
        except:
            data = {'code':'500', 'pass':'No'}
            result = json.dumps(data)
            return result
    else:
        data = {'code':'500', 'feature':'non living body, try again'}
        result = json.dumps(data)
        return result

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=6175, debug=False)

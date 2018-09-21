# coding=utf-8
from datetime import timedelta
from flask import Flask,request,session, app
import os
import sys
import ctypes
import time
import numpy as np

from PIL import Image
import json

EXTS = 'jpg', 'jpeg', 'JPG', 'JPEG', 'gif', 'GIF', 'png', 'PNG'

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
    feature = ""
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
    (im1_width, im1_height) = im2.size
    for i in range(num):
        im1_part = im1.crop((i*(im1_width / num), i*(im1_height / num), (i+1)*(im1_width / num), (i+1)*(im1_height / num)))
        distance = hamming(avhash(im1_part), long(im2_feature))
        if distance > 2560:
            return "No"
    return "Yes"

app = Flask(__name__)
# app.config['SECRET_KEY'] = '123456'

# @app.before_request
# def make_session_permanent():
#     session.permanent = True
#     app.permanent_session_lifetime = timedelta(minutes=0.2)

@app.route('/hash', methods=["POST", "GET"])
def function_hash():
    im1 = str(request.files.get("im1"))
    im2feature = str(request.values.get("im2feature"))
    print(im1 + '   ' + im2feature)
    if (im1 == "None" or im2feature == "None"):
        return "Wrong args"
    '''
    new_im1 = im1.split(".")[0]+"_r."+im1.split(".")[1]
    libb = ctypes.cdll.LoadLibrary("./projection.so")
    res = libb.projection(im1, new_im1)
    if res == -1:
        # exception
        code = '500'
        pass_value = "�~[��~I~G�| ���~O�~T~Y误��~L请��~S�~E���~@��| �~M���| 为中��~C��~L没�~\~I�~E���~V红�~I�干�~I��~Z~D�~[��~I~G��~L��~T�~M���| �~Q��~[��~U~Y�~\~I足��~_大�~Z~D空�~Z~Y"
    else:
        code = '200'
        pass_value = do_hash(new_im1, im2_feature, grain)
    '''
    data = {'code':200, 'pass':'Yes'}
    result = json.dumps(data)
    return result


@app.route('/save', methods=["POST", "GET"])
def function_save():
    im1 = str(request.files.get("im1"))
    
    print im1
    if im1 == "None":
        return "No args"
    grain = bool(request.values.get("grain"))
    print grain
    '''
    new_im1 = im1.split(".")[0]+"_r."+im1.split(".")[1]
    libb = ctypes.cdll.LoadLibrary("./projection.so")
    res = libb.projection(im1, new_im1)
    if res == -1:
        # exception

        code = '500'
    '''
  
    feature = "�~[��~I~G�| ���~O�~T~Y误��~L请��~S�~E���~@��| �~M���| 为中��~C��~L没�~\~I�~E���~V红�~I�干�~I��~Z~D�~[��~I~G��~L��~T�~M���| �~Q��~[��~U~Y�~\~I足��~_大�~Z~D空�~Z~Y"
   # else:
    #    code = '200'
     #   feature = create_hash(new_im1, grain)

    data = {'code':'200', 'feature':feature}
    result = json.dumps(data)
    return result

@app.route('/upload', methods=["POST", "GET"])
def up_load():

    img = str(request.files.get("img"))
    print(img)
    name = request.values.get('name')
    print(name)
    res = {'code': '200'}
    result = json.dumps(res)    
    return result

@app.route('/face', methods=["POST", "GET"])
def function_face():
    im1 = str(request.files.get("im1"))
    print im1
    if im1 != "None":
	'''
        os.system("cp %s ./project/1.png" % im1)
	
        while True:
            if os.path.exists("./project/myfeature.txt"):
                feature = open("./project/myfeature.txt").read()
                os.remove("./project/myfeature.txt")
                break
            else:
                continue
	'''
        data = {'code':'200', 'feature':'12651313513'}
        result = json.dumps(data)
	print("ok")
        return result
    else:
        return 'No images', 300

@app.route('/check', methods=["POST", "GET"])
def function_check():
    im = str(request.files.get("img"))
    feature = request.values.get("feature")
    print im
    if im == "None":
        return "No args"
    # os.system("cp %s ./project/1.png" % im)
    '''
     while True:
        if os.path.exists("./project/myfeature.txt"):
            feature = open("./project/myfeature.txt").read()
            os.remove("./project/myfeature.txt")
            break
        else:
            continue
    '''
    data = {'code':'200'}
    result = json.dumps(data)
    return result

@app.route('/check_bk', methods=["POST", "GET"])
def function_check_bk():
        im1 = str(request.values.get("im1"))
        im2 = str(request.values.get("im2feature"))
        if (im1 == "None" or im2 == "None"):
            return "No args"
        im2feature = im2.replace("+", " ")
        checkfeature = np.array([float(item) for item in im2feature.split()])
        os.system("cp %s ./project/1.png" % im1)
        while True:
                if os.path.exists("./project/myfeature.txt"):
                        feature = np.array([float(item) for item in open("./project/myfeature.txt").read().split()])
                        os.remove("./project/myfeature.txt")
                        break
                else:
                        continue
        checkfeature.astype(feature.dtype)
        score = np.dot(checkfeature - feature,checkfeature - feature)/np.dot(checkfeature, checkfeature)

        if score < 0.6:
                code = '200'
                pass_value = "yes"
        else:
                code = '500'
                pass_value = "no"

        data = [{'code':code, 'pass':pass_value}]
        result = json.dumps(data)
        return result

if __name__ == '__main__':
    '''
    im1 = "/data1/an_zhao/lab/paintings/paper/2-6.JPG"
    grain = True
    print do_hash(im1, im2_feature, grain)
    '''
    app.run(host='0.0.0.0', port=6175, debug=False)

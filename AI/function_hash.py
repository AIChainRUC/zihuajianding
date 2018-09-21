
# coding: utf-8

from SimpleXMLRPCServer import SimpleXMLRPCServer
from SocketServer import ThreadingMixIn
from xmlrpclib import ServerProxy
import thread
import glob
import os
import sys
import ctypes

from PIL import Image

EXTS = 'jpg', 'jpeg', 'JPG', 'JPEG', 'gif', 'GIF', 'png', 'PNG'


class ThreadXMLRPCServer(ThreadingMixIn, SimpleXMLRPCServer):
    pass


class RPCServer():
    def __init__(self, ip='127.0.0.1', port='6174'):
        self.ip = ip
        self.port = int(port)
        self.svr = None

    def start(self, func_lst):
        thread.start_new_thread(self.service, (func_lst, 0,))

    def resume_service(self, v1, v2):
        self.svr.serve_forever(poll_interval=0.001)

    def service(self, func_lst, v1):
        self.svr = ThreadXMLRPCServer((self.ip, self.port), allow_none=True)
        for func in func_lst:
            self.svr.register_function(func)
        self.svr.serve_forever(poll_interval=0.001)

    def activate(self):
        thread.start_new_thread(self.resume_service, (0, 0,))

    def shutdown(self):
        try:
            self.svr.shutdown()
        except Exception, e:
            print 'rpc_server shutdown:', str(e)


class RPCClient():
    def __init__(self, ip='127.0.0.1', port='6174'):
        self.svr = ServerProxy('http://' + ip + ':' + port + '/', allow_none=True, use_datetime=True)

    def get_svr(self):
        return self.svr

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
        if distance > 2560:
            return False
    return True

def mainstream(im1, im2_feature, grain):
    new_im1 = im1.split(".")[0]+"_r."+im1.split(".")[1]
    libb = ctypes.cdll.LoadLibrary("./projection.so")
    res = libb.projection(im1, new_im1)
    if res == -1:
        # exception
        result = "图片格式错误，请输入一张印章为中心，没有其他红色干扰的图片，且印章周围留有足够大的空隙"
        return result
    else:
        return do_hash(new_im1, im2_feature, grain)

if __name__ == "__main__":
    # fw = open('1.txt', 'r')
    # strr = fw.readline()
    # fw.close()
    # out = mainstream('/data1/an_zhao/lab/paintings/paper/3-4.jpg', strr, True)
    # print out
    r = RPCServer('0.0.0.0', '6174')
    r.service([mainstream], 0)

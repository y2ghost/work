# coding=utf8

from twisted.application.service import ServiceMaker

finger = ServiceMaker('finger example', 'finger.tap', 'Run a finger service', 'finger')


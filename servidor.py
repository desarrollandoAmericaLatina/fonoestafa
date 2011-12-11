#!/usr/bin/env python
from urlparse import urlparse
import time
from SimpleHTTPServer import SimpleHTTPRequestHandler
import SocketServer
PORT = 8000


class RodHTTPHandler(SimpleHTTPRequestHandler):
	def do_GET(self):
		args = urlparse(self.path)
		if args.path != '/getinfo':
			return SimpleHTTPRequestHandler.do_GET(self)
		print '...'
		#time.sleep(3)
		query = args.query.split('&')
		for param in query:
			key, val = param.split('=')
			if key == 'number':
				print 'consultan por', val
				val = int(val)
				if (val%2 != 0):
					
					print val, ' es numero denunciado'
					return self.response(msg = 'si;hoy')
				else:
					return self.response(msg = 'False')

	def do_POST(self):
		args = urlparse(self.path)
		if args.path != '/denounce':
			return self.response(code=400)

		print 'llego un POST!!'
		print '...'
		#time.sleep(3)
		print self.headers
		return self.response(msg='OK')

		

	def response(self, msg='', code=200, header=('Content-type', 'text/plain')):
		self.send_response(code)
		self.send_header(header[0], header[1])
		self.end_headers()
		self.wfile.write(msg)


Handler = RodHTTPHandler
httpd = SocketServer.TCPServer( ("", PORT), Handler )

try:
	httpd.serve_forever()
except KeyboardInterrupt:
	print 'fin'

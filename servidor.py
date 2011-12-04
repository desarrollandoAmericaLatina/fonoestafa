#!/usr/bin/env python
from urlparse import urlparse
from SimpleHTTPServer import SimpleHTTPRequestHandler
import SocketServer
PORT = 8000


class RodHTTPHandler(SimpleHTTPRequestHandler):
	def do_GET(self):
		args = urlparse(self.path)
		if args.path != '/getinfo':
			return SimpleHTTPRequestHandler.do_GET(self)

		query = args.query.split('&')
		for param in query:
			key, val = param.split('=')
			if key == 'num':
				print 'consultan por', val
				val = int(val)
				if (val%2 == 0):
					print val, ' es numero denunciado'
					return self.response(msg = 'True;hoy dia mismo')
				else:
					return self.response(msg = 'False')

	def response(self, msg, code = 200, header = ('Content-type', 'text/plain')):
		self.send_response(code)
		self.send_header(header[0], header[1])
		self.end_headers()
		self.wfile.write(msg)


Handler = RodHTTPHandler
httpd = SocketServer.TCPServer( ("", PORT), Handler )
httpd.serve_forever()

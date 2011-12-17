#!/usr/bin/env python
from urlparse import urlparse
import time
from SimpleHTTPServer import SimpleHTTPRequestHandler
import SocketServer
PORT = 8000


LAST_DAY_NUMBER = 1
LAST_PHN_NUMBER = 996601

class RodHTTPHandler(SimpleHTTPRequestHandler):
	def make_dates(self):
		global LAST_DAY_NUMBER, LAST_PHN_NUMBER
		d1 = '%d;2011-12-%02d 10:20:30' % ((LAST_PHN_NUMBER + 0), (LAST_DAY_NUMBER + 0))
		d2 = '%d;2011-12-%02d 10:20:30' % ((LAST_PHN_NUMBER + 1), (LAST_DAY_NUMBER + 1))
		d3 = '%d;2011-12-%02d 10:20:30' % ((LAST_PHN_NUMBER + 2), (LAST_DAY_NUMBER + 2))
		LAST_DAY_NUMBER += 3
		LAST_PHN_NUMBER += 3
		return [d1, d2, d3]

	
	def do_GET(self):
		args = urlparse(self.path)
		if args.path == '/hustler/ask':
			query = args.query.split('&')
			for param in query:
				key, val = param.split('=', 1)
				if (key == 'number'):
					print 'consultan por', val
					num = int(val)
					if ((num % 2) != 0):
						print num,'es numero denunciado!!!'
						return self.response(msg=['si;hoy'] + self.make_dates())
					else:
						return self.response(msg='no')
				else:
					print key, ' --> ', val

		elif args.path == '/hustler/create':
			print 'denuncia!!!'
			query = args.query.split(',')
			for param in query:
				print ' ', param
			return self.response()

		elif args.path == '/hustler/updates':
			print 'updates!!!'
			query = args.query.split(',')
			for param in query:
				print ' ', param
			return self.response(msg=self.make_dates())

		elif args.path == '/hustler/status':
			print 'status!!!'
			if LAST_DAY_NUMBER == 1:
				msg = 'EMPTY'
			else:
				msg = 'WITH DATA'
			print msg
			return self.response(msg=msg)

		else:
			return SimpleHTTPRequestHandler.do_GET(self)


	def do_POST(self):
		args = urlparse(self.path)
		if args.path != '/create':
			return self.response(code=400)

		print 'llego un POST!!'
		print '...'
		print self.headers
		return self.response(msg='OK')

		

	def response(self, msg='', code=200, header=('Content-type', 'text/plain')):
		self.send_response(code)
		self.send_header(header[0], header[1])
		self.end_headers()
		if type(msg) == list:
			msg = '\n'.join(msg)
		self.wfile.write(msg)


Handler = RodHTTPHandler
httpd = SocketServer.TCPServer( ("", PORT), Handler )

try:
	httpd.serve_forever()
except KeyboardInterrupt:
	print 'fin'

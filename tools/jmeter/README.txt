jmeter testsuite readme
============================================================
$Author: mbae $ 
$Date: 2005/11/30 00:00:19 $ 
$Revision: 1.1.2.1 $
============================================================

-1. hostname and port
-----------------------------------------------------------
Setting hostname and port in jmeter scripts is currently a 
problem. You could certainly open the script with the gui tool
and set the hostname in the http default confiuration element
to your needs. As a workaround is set hostname and port to 

   jmeter.itracker.org:8080
   
Run your itracker on port 8080, edit your /etc/hosts file 
and enter the hostname to let it work 'out-of-the-box'. 
Sorry about this. (i will read about tag replacing in ant 
to try to solve the problem; hold on.)
   

0. files and directories
------------------------------------------------------------
README.txt           # this file
lib/ant-jmeter.jar   # lib directory with ant task to run jmeter
xslt/                # stylesheets transform log to other formats
tests/               # directory for test scripts (.jmx)
log/                 # directory for log output
html/                # directory for html transformations


1. jmeter installation
----------------------------------------------------------
To run the test you must have a working jmeter installation. 
I tested it with jmeter versions 2.0.2 and 2.1.1 on linux.

http://jakarta.apache.org/site/downloads/downloads_jmeter.cgi


2. ant integration
-----------------------------------------------------------
JMeter tests are integrationed in the build.xml file of 
itracker. this allows a convenient way to run the test. As 
an alternative jmeter tests could be run with the gui.

2.1. build.properties
A property with 'jmeter.home' must be added to the
build.properties. The value should point to the home directory
of jmeter:

   jmeter.home=/opt/jakarta/jakarta-jmeter-2.0.2/
   
2.2. ant output
By default the ant target prints many debug statements to the console.
To avoid this you could eidt the jmetertest.properties file in the
bin directory of your jmeter installation. Change the log_level.jmeter
property to an appropriate value (eg. WARN) to shut jmeters mouth.


3. jmeter tests
-----------------------------------------------------------
Currently there is a single jmeter test 

   /tests/blackbox-all.jmx
   
Hopefully there will be more once.

   
4. test output
-----------------------------------------------------------
There is two kind of output while running the jmeter targets.
First a log is written to the 'log' directory of the testsuite.
Second this log will be translated to HTML. HTML output goes
to the 'html' directory of the testsuite;

   log/blackbox-all-yyyyMMdd-hhmm.jtl  # log as xml
   html/blackbox-all-yyyyMMdd-hhmm.html  # html report
   html/blackbox-all-detail-yyyyMMdd-hhmm.html  # html report


==========================================================================
Changes so far: 
$Log: README.txt,v $
Revision 1.1.2.1  2005/11/30 00:00:19  mbae
adding jmeter testsuite

==========================================================================
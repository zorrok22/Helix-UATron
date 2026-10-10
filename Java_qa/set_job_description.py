#import necessary modules  
from bs4 import BeautifulSoup
import sys,os,ssl,csv,shutil

"""
Utility to be used for Team dashboard to show tests count in terms of total/passed/failed.
Usage: python BuildDescriptor.py <suitename> <job_output_htmlpath> <csvfilename> <framework html>
You can specify either output path where output html is generated or the full path including the html file name
[Vivek] this is for old extent report
"""

def analyze_testsuites_count_fromHTML(suitename,outputfilename):
    '''
    Analyze and count total,passed and failed testsuites count for jobs
    :param outputfilename:
    '''
    # print (outputfilename)

    if not outputfilename.endswith(".html"):
        print ("Output xml not provided using default name report xml ")
   
    try : 
        passed_tests = 0
        failed_tests = 0
        total_tests = 0
        soup = BeautifulSoup(open(outputfilename),'html.parser')
        tableoffile = soup.find("div", id='test-view-charts').find("div", class_='card-panel').find_all("div",class_='block text-small')
        #print("value", tableoffile[0].find('span').get_text().strip().split(" ")[0])
        passed_tests = int(tableoffile[0].find('span').get_text().strip().split(" ")[0])
        failed_tests = int(tableoffile[1].find('span').get_text().strip().split(" ")[0])
        others =int(tableoffile[1].find_all('span')[1].get_text().strip())
        # Calculate failed TC count
        failed_tests = failed_tests +others
        # Calculate total TC count
        total_tests = passed_tests + failed_tests
        #Calculate pass percentage
        pass_percent = (passed_tests/total_tests)*100
        print ("Total:" + str(total_tests) + " Passed:" + str(passed_tests) + " Failed:" + str(failed_tests) + " Pass rate: " + str(round(pass_percent,2)) + "%")
        #print ("Pass rate: " + str(round(pass_percent,2)) + "%")
		                       
		
    except IOError as errno:
        # print ("I/O error({0}): {1}, Please provide correct input file path" + errno)
        quit()	

# Check for the arguments
if 4 != sys.argv.__len__():
    print ("Please provide all arguments..")
    print ("Usage: python hb_testresultscollectorscript.py <suiteName> <job_output_path> HTML")
    quit()

# If testsuite type - TestNG then call the function
if str(sys.argv[3]).strip().upper() == "HTML":
    analyze_testsuites_count_fromHTML(sys.argv[1],sys.argv[2])
	
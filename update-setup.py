#!/usr/bin/python
import sys
import os
import subprocess
import shutil
import socket
from os.path import expanduser

# Settings

home = expanduser("~")
hostname = socket.gethostname()
if sys.platform == "win32":
	JENKINS_SLAVE_HOME="D:/kderoot/scripts/"
	EMERGE_ETC="D:/kderoot/etc/"
	EMERGE_BASE="D:/kderoot"
elif sys.platform == "darwin":
	JENKINS_SLAVE_HOME= home + "/scripts"
	scriptsLocation= home + "/scripts"
else:
	JENKINS_SLAVE_HOME= home + "/scripts"
	scriptsLocation= home + "/scripts"
JENKINS_BRANCH="master"
JENKINS_DEPENDENCY_BRANCH="master"

def getRepository(repoPath, repoUrl, repoBranch="sok-work"):
	if not os.path.exists(repoPath):
		os.mkdir(repoPath)

	originalDir = os.getcwd()
	os.chdir(repoPath)

	if not os.path.exists(".git"):
		subprocess.call("git clone " + repoUrl + " .", shell=True)

	subprocess.call("git pull", shell=True)
#	subprocess.call("git checkout " + repoBranch, shell=True)
#	subprocess.call("git merge --ff-only origin/" + repoBranch, shell=True)

	os.chdir(originalDir)

getRepository(JENKINS_SLAVE_HOME, "git://anongit.kde.org/kde-build-metadata", JENKINS_BRANCH)
getRepository(os.path.join(JENKINS_SLAVE_HOME, "dependencies"), "git://anongit.kde.org/kde-build-metadata", JENKINS_DEPENDENCY_BRANCH)
getRepository(os.path.join(JENKINS_SLAVE_HOME, "poppler-test-data"), "git://git.freedesktop.org/git/poppler/test", JENKINS_DEPENDENCY_BRANCH)
getRepository(os.path.join(JENKINS_SLAVE_HOME, "kapidox"), "git://anongit.kde.org/kapidox", JENKINS_DEPENDENCY_BRANCH)

if sys.platform == "win32":
	settingsfile = JENKINS_SLAVE_HOME + "etc/kdesettings.ini"
	dstroot = EMERGE_ETC
	print "Copying " + settingsfile + " to " + dstroot
	try:
	  shutil.copy(settingsfile, dstroot)
	except:
	  print "Oh No! Something went wrong"
	else: 
	  print "Copy was successful"
	envfile = JENKINS_SLAVE_HOME + "emerge/kdeenv.bat"
	dstroot = EMERGE_BASE
	print "Copying " + envfile + " to " + dstroot 
	try:
	  shutil.copy(envfile, dstroot)
	except:
	  print "Oh No! Something went wrong"
	else: 
	  print "Copy was successful"
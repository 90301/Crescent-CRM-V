#!/bin/bash

### Constants
RIGHT_NOW=$(date +%s)

##### Backup Script

	mkdir $RIGHT_NOW
	cd $RIGHT_NOW
	echo "Cloning git repository Crescent-CRM-V..."
	git clone https://github.com/90301/Crescent-CRM-V.git
	echo "Clone successful"
	exit 3


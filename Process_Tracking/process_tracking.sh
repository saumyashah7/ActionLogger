SECONDS=0
if [ $# -ne 1 ]; then
	echo "Please provide only the name of the process to be tracked"
	exit
fi
file=$1_tracking.txt
procfile=$1_running.txt 
if [ -f $file ]; then
	rm -rf #file
fi
app=$1
pck=3 #process check time in seconds
fst=10 #file send time in seconds

function sendUsage(){
	echo "Send usage called!!!"
	pgrep $app > $procfile
	#zero=0
	count=$(grep -F -x -v -f $procfile $file | sort -u | wc -l)
	#count=$(sort -u $file | wc -l)
	if [[ $count -gt 0 ]]; then
		tok=$(curl -s -X GET https://eagerapp1.herokuapp.com/getToken)
		curl -H 'Content-Type: application/json' -d "{"application":"${app}","count":"${count}","token":"${tok}"}" -X POST https://eagerapp1.herokuapp.com/log
		grep -F -x -f $procfile $file > $1_temp_tracking.txt
		rm -rf $file
		mv $1_temp_tracking.txt $file
	else
		echo "Nothing to send"
	fi
	rm -rf $procfile
}

while true; do
	#ps aux | grep $1 | grep -v grep | awk '{print $2}' >> $file
	pgrep $1 >> $file
	sleep $pck 
	if [ $SECONDS -gt $fst ]; then
		SECONDS=0
		sendUsage
	fi
	echo $SECONDS
done

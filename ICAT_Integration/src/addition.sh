a=1
b=1
function add(){
	bash actionlogger.sh addition two_numbers # addition->application_name, two_numbers->metric_name 
	echo $(($a+$b))
}
c=$(add)
echo "$c"

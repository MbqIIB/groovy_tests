def suffixes = ["DESARROLLO","RELEASE","MANTENIMIENTO","DEVELOPMENT"];

def stream = "MELME - MELMELADA - DESARROLLO"

for(a in suffixes) {
	stream = stream.split("- ${a}")[0].split("-${a}")[0];
	
}

println(stream);

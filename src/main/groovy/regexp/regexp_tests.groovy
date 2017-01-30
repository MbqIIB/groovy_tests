import java.util.regex.*;

Matcher myMatcher = Pattern.compile(/[0-9]\.[0-9]\.[0-9]\.[0-9]$/).matcher("8.7.4.0");

println("Matcher:");
if(myMatcher) {
	println("SI");
} else {
	println("NO");
}
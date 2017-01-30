def file1 = new File("C:\\Users\\dcastro.jimenez\\Desktop\\Docker Workflows\\file1.txt");
def file2 = new File("C:/Users/dcastro.jimenez/Desktop/Docker Workflows/file1.txt");


println(file1.getCanonicalPath())
println(file2.getAbsolutePath())

println(file1.getCanonicalPath().equals(file2.getCanonicalPath()));
println(file1.getAbsolutePath().equals(file2.getAbsolutePath()));

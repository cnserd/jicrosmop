package rosmop.codegen;

import java.io.FileNotFoundException;
import java.util.HashMap;

import rosmop.ROSMOPException;
import rosmop.RVParserAdapter;
import rosmop.parser.ast.mopspec.Event;
import rosmop.util.Tool;

import com.runtimeverification.rvmonitor.c.rvc.CSpecification;
import com.runtimeverification.rvmonitor.logicpluginshells.LogicPluginShellResult;

public class HeaderGenerator {

	protected final static SourcePrinter printer = new SourcePrinter();
	static boolean hasInit = false;

	public static void generateHeader(HashMap<CSpecification, LogicPluginShellResult> toWrite, String outputPath) throws FileNotFoundException, ROSMOPException{

		String hFile = "rvmonitor.h";
		String hDef = "RVCPP_RVMONITOR_H";

		printer.printLn("#ifndef " + hDef);
		printer.printLn("#define " + hDef + "\n");

		printRosIncludes();
		for (CSpecification rvcParser : toWrite.keySet()) {
			printer.printLn(rvcParser.getIncludes());
		}

		printer.printLn();
		printer.printLn("namespace rv");
		printer.printLn("{");
		printer.indent();

		// Inner monitor class declaration
		printer.printLn("class " + GeneratorUtil.MONITOR_CLASS_NAME);
		printer.printLn("{");
		printer.indent();

		//public methods
		printer.printLn("public:");
		printer.indent();

		//Constructor
		printer.printLn(GeneratorUtil.MONITOR_CLASS_NAME + "(std::string topic, ros::SubscribeOptions &" 
				+ GeneratorUtil.SUBSCRIBE_OPTIONS + ");");

		//Deconstructor
		printer.printLn("~" + GeneratorUtil.MONITOR_CLASS_NAME + "();");
		printer.printLn();

		for (CSpecification rvcParser : toWrite.keySet()) {
			if(!hasInit && !((RVParserAdapter) rvcParser).getInit().isEmpty()){
				printer.printLn("void init();");
				hasInit = true;
			}
			
			if(toWrite.get(rvcParser) != null){
				printer.printLn((String) toWrite.get(rvcParser).properties.get("header declarations"));
			}else{
				for(Event event : ((RVParserAdapter) rvcParser).getEventsList()){
					printer.printLn("void monitorCallback_" + event.getName() + event.getDefinition() + ";");
				}
			}
		}

		printer.printLn();

		printer.unindent();
		printer.printLn("private:");
		printer.indent();

		//        std::string topic_name;
		//        boost::shared_ptr<rv::ServerManager> server_manager;

		printer.printLn("std::string " + GeneratorUtil.TOPIC_PTR_NAME + ";");
		printer.printLn("boost::shared_ptr<rv::ServerManager> " + GeneratorUtil.SERVERMANAGER_PTR_NAME + ";");
		printer.printLn();

		printer.unindent();

		printer.unindent();
		printer.printLn("};");
		printer.unindent();

		printer.printLn("}");
		printer.unindent();

		printer.printLn();

		printer.printLn("#endif");

		Tool.writeFile(printer.getSource(), outputPath+hFile);
	}

	private static void printRosIncludes() {
		printer.printLn("#include \"rv/xmlrpc_manager.h\"");
		printer.printLn("#include \"rv/connection_manager.h\"");
		printer.printLn("#include \"rv/server_manager.h\"");
		printer.printLn("#include \"rv/subscription.h\"");
		printer.printLn("#include \"ros/publication.h\"");
		printer.printLn("#include \"std_msgs/String.h\"");
		printer.printLn("#include \"ros/subscribe_options.h\"");
		printer.printLn("#include \"ros/advertise_options.h\"");
		printer.printLn("#include \"ros/callback_queue.h\"");
		printer.printLn("#include <rosgraph_msgs/Log.h>");
		printer.printLn("#include <boost/scoped_ptr.hpp>");
		printer.printLn("#include <ros/serialization.h>");		
	}
}

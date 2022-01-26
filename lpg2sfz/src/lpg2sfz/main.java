package lpg2sfz;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class main {

	public static void lpgConverter(String lpgFile) {

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		try {
			dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

			DocumentBuilder db = dbf.newDocumentBuilder();

			File graceFile = null;

			graceFile = new File(lpgFile);

			String fileNameWithOutExt = (graceFile.getName()).replaceFirst("[.][^.]+$", "");
			//prepares the SFZ file
			FileWriter outSFZ = new FileWriter(graceFile.getAbsolutePath() + ".sfz");

			Document doc = db.parse(graceFile);

			doc.getDocumentElement().normalize();

			//basic sfz config
			outSFZ.write("<global>\n"
					+ "ampeg_attack=0\n"
					+ "ampeg_release=0.0001\n"
					+ "\n");

	        //List the number of <SampleGroup> elements in the file -------------------------------------
	        NodeList sampleGroupList = doc.getElementsByTagName("SampleGroup");

	        //for loop
	        for (int i = 0; i < sampleGroupList.getLength(); i++) {

	        	//Specify the item number of <SampleGroup> element
	        	Node sampleGroupNode = sampleGroupList.item(i);      	

	        	//Verify if the node type is a Element
	        	if (sampleGroupNode.getNodeType() == Node.ELEMENT_NODE) {

	        		//Converts the SampleGroup node into a SampleGroup element
	        		Element sampleGroupElement = (Element) sampleGroupNode;

	        		//Getting the text of <Name> inside of <SampleGroup>
	        		Node nameGroupNode = sampleGroupElement.getElementsByTagName("Name").item(0);
	        		String nameGroupString = nameGroupNode.getTextContent();

	        		outSFZ.write("<group> //" + nameGroupString + "\n");

	        		//<Voice Parameters> - Converts the current SampleGroup node into a element
	        		Element voiceParametersElement = (Element) sampleGroupNode;
	        		//Selects the only <VoiceParameters> element inside of the current SampleGroup
	        		voiceParametersElement.getElementsByTagName("VoiceParameters").item(0);

		        	if (voiceParametersElement.getNodeType() == Node.ELEMENT_NODE) {

		        			//PitchTracking
							Node pitchTrackingNode = voiceParametersElement.getElementsByTagName("PitchTracking").item(0);
							String pitchTrackingString = pitchTrackingNode.getTextContent();

							if (pitchTrackingString.equals("Note")) {
								outSFZ.write("pitch_keytrack=100\n");
							} else if (pitchTrackingString.equals("BPM")){
								outSFZ.write("pitch_keytrack=100\n"); 
							} else outSFZ.write("pitch_keytrack=0\n");

							//SamplerTriggerMode
							Node samplerTriggerModeNode = voiceParametersElement.getElementsByTagName("SamplerTriggerMode").item(0);
							String samplerTriggerModeString = samplerTriggerModeNode.getTextContent();

							if (samplerTriggerModeString.equals("OneShot")) {
								outSFZ.write("loop_mode=one_shot\n\n");
							} else if (samplerTriggerModeString.equals("LoopOff")) {
								outSFZ.write("loop_mode=no_loop\n\n");
							} else if (samplerTriggerModeString.equals("LoopContinuous")) {
								outSFZ.write("loop_mode=loop_continuous\n\n");
							} else if (samplerTriggerModeString.equals("LoopSustain")) {
								outSFZ.write("loop_mode=loop_sustain\n\n");
							} else outSFZ.write("loop_mode=no_loop\n\n");

		        	}

	        		//List all <Region> elements inside of current <SampleGroup>
	        		NodeList regionList = sampleGroupElement.getElementsByTagName("Region");

	        		for (int j = 0; j < regionList.getLength(); j++) {
	        			//Selects the current Region
	        			Node regionNode = regionList.item(j);

	        			outSFZ.write("<region> //" + j +"\n");

		        		if (regionNode.getNodeType() == Node.ELEMENT_NODE) {

		        			//<SampleProperties>
		        			Element samplePropertiesElement = (Element) regionNode;

		        			samplePropertiesElement.getElementsByTagName("SampleProperties");

		        			if (samplePropertiesElement.getNodeType() == Node.ELEMENT_NODE) {
		        				//Getting the name file of the sample in <SampleProperties>
		        				Node sampleFileNameNode = samplePropertiesElement.getElementsByTagName("SampleFileName").item(0);
		        				String sampleNameString = sampleFileNameNode.getTextContent();

		        						outSFZ.write("sample=" + fileNameWithOutExt + " Samples/" + sampleNameString + "\n\n");
		        			}

		        			//<RegionProperties>
		        			Element regionPropertiesElement = (Element) regionNode;

		        			regionPropertiesElement.getElementsByTagName("RegionProperties");

		        			if (regionPropertiesElement.getNodeType() == Node.ELEMENT_NODE) {

		        				//LowNote
		        				Node lowNoteNode = regionPropertiesElement.getElementsByTagName("LowNote").item(0);
		        				String lowNoteString = lowNoteNode.getTextContent();

		        					outSFZ.write("lokey=" + lowNoteString + "\n");

		        				//HighNote
		        				Node highNoteNode = regionPropertiesElement.getElementsByTagName("HighNote").item(0);
		        				String highNoteString = highNoteNode.getTextContent();

		        					outSFZ.write("hikey=" + highNoteString + "\n");

		        				//LowVelocity
		        				Node lowVelocityNode = regionPropertiesElement.getElementsByTagName("LowVelocity").item(0);
		        				String lowVelocityString = lowVelocityNode.getTextContent();

		        					outSFZ.write("lovel=" + lowVelocityString + "\n");

		        				//HighVelocity
		        				Node highVelocityNode = regionPropertiesElement.getElementsByTagName("HighVelocity").item(0);
		        				String highVelocityString = highVelocityNode.getTextContent();

		        					outSFZ.write("hivel=" + highVelocityString + "\n");

		        				//RootNote
		        				Node rootNoteNode = regionPropertiesElement.getElementsByTagName("RootNote").item(0);
		        				String rootNoteString = rootNoteNode.getTextContent();

		        					outSFZ.write("pitch_keycenter=" + rootNoteString + "\n");

		        				//SampleStart
		        				Node sampleStartNode = regionPropertiesElement.getElementsByTagName("SampleStart").item(0);
		        				String sampleStartString = sampleStartNode.getTextContent();

		        					outSFZ.write("offset=" + sampleStartString + "\n");

		        				//SampleVolume
		        				Node sampleVolumeNode = regionPropertiesElement.getElementsByTagName("SampleVolume").item(0);
		        				String sampleVolumeString = sampleVolumeNode.getTextContent();

		        					outSFZ.write("volume=" + sampleVolumeString + "\n");

		        				//SamplePan
		        				Node samplePanNode = regionPropertiesElement.getElementsByTagName("SamplePan").item(0);
		        				String samplePanString = samplePanNode.getTextContent();

		        					outSFZ.write("pan=" + samplePanString + "\n");

		        				//SampleTune
		        				Node sampleTuneNode = regionPropertiesElement.getElementsByTagName("SampleTune").item(0);
		        				String sampleTuneString = sampleTuneNode.getTextContent();

		        					outSFZ.write("transpose=" + sampleTuneString + "\n");

		        				//SampleFine
		        				Node sampleFineNode = regionPropertiesElement.getElementsByTagName("SampleFine").item(0);
		        				String sampleFineString = sampleFineNode.getTextContent();

		        					outSFZ.write("tune=" + sampleFineString + "\n\n");
		        			}
		        		}
	        		}
	        	}
	        } 
	        outSFZ.close(); System.out.println("Successfully converted.");
		} catch (ParserConfigurationException | SAXException | IOException e) { System.out.println("an error just ocurred: " + e); e.printStackTrace(); }
		System.exit(0);
	}


	public static void main(String[] args) throws IOException, InterruptedException  {
		if (args.length == 0) {
			System.out.println("Please specify a Grace (.lpg) file.");
			System.exit(0);
		}
		lpgConverter(args[0]);
	}
}
package gate.starter;

import gate.GateAuthConnection;
import gate.GateLogicConnection;
import gate.GateServer;
import org.apache.commons.cli.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.File;

/**
 * Created by Qzy on 2016/1/28.
 */
public class GateStarter {
//    private static final Logger logger = LoggerFactory.getLogger(GateStarter.class);
    private static File cfg = null;
    private static File log = null;
    private static int gateId;

//    @Value("${gate.server.ip}")
    private String gateIp;
//    @Value("${gate.server.port}")
    private int gatePort = 9090;

    public static void main(String[] args) throws Exception {

//        configureAndStart(args);
        GateStarter gateStarter = new GateStarter();
//        gateStarter.configureAndStart(args);
        gateStarter.start();
    }

    public void start() {
        new Thread(() -> GateServer.startGateServer(gatePort)).start();
    }

    public void configureAndStart(String[] args) throws ParseException {
//        parseArgs(args);

        try {
            //parse xml File and apply it
            DocumentBuilder builder = DocumentBuilderFactory
                    .newInstance().newDocumentBuilder();
            Document doc = builder.parse(cfg);
            Element rootElement = doc.getDocumentElement();

            XPath xPath = XPathFactory.newInstance().newXPath();
            XPathExpression xPathExpression = null;
            NodeList nodeList = null;
            Element element = null;

            xPathExpression = xPath.compile("/gate/id");
            nodeList = (NodeList) xPathExpression.evaluate(rootElement, XPathConstants.NODESET);
            element = (Element) nodeList.item(0);
            gateId = Integer.parseInt(element.getAttribute("value"));
//            logger.info("gate id " + gateId);

//            logger.info("gateserver gateListenPort {}", gateIp);

            xPathExpression = xPath.compile("/gate/auth");
            nodeList = (NodeList) xPathExpression.evaluate(rootElement, XPathConstants.NODESET);
            element = (Element) nodeList.item(0);
            String authIP = element.getAttribute("ip");
            int authPort = Integer.parseInt(element.getAttribute("port"));
//            logger.info("GateAuthConnection auth ip: {}  auth port: {}", authIP, authPort);

            xPathExpression = xPath.compile("/gate/logic");
            nodeList = (NodeList) xPathExpression.evaluate(rootElement, XPathConstants.NODESET);
            element = (Element) nodeList.item(0);
            String logicIP = element.getAttribute("ip");
            int logicPort = Integer.parseInt(element.getAttribute("port"));
//            logger.info("GateLogicConnection logic ip: {}  logic port: {}", logicIP, logicPort);

            //TODO init log congfigres

            //Now Start Servers
            new Thread(() -> GateServer.startGateServer(gatePort)).start();

            new Thread(() -> GateAuthConnection.startGateAuthConnection(authIP, authPort)).start();

            new Thread(() -> GateLogicConnection.startGateLogicConnection(logicIP, logicPort)).start();

        } catch (Exception e) {
//            logger.error("init cfg error");
            e.printStackTrace();
        }
    }

    public static int getGateId() {
        return gateId;
    }
}

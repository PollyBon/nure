package ua.nure.agent

import java.util

import akka.actor._
import ua.nure.world.Dictionary
import ua.nure.world.PathNode

class NavigatorAgent extends Actor {

    var NAVIGATOR_AGENT = "navigator"

    var worldMap: util.HashMap[AID, PathNode]

    def setup() {
        System.out.println("NavigatorAgent " + getAID().getName() + " started")
        DFAgentDescription dfd = new DFAgentDescription()
        dfd.setName(getAID())
        ServiceDescription sd = new ServiceDescription()
        sd.setType(NAVIGATOR_AGENT)
        sd.setName(getClass().getName())
        dfd.addServices(sd)
        try {
            DFService.register(this, dfd)
        }
        catch (FIPAException e) {
            e.printStackTrace()
        }

        addBehaviour(new ControlSpeleologistBehaviour())
        worldMap = new HashMap<>()
    }

    class ControlSpeleologistBehaviour extends CyclicBehaviour {
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM)
            ACLMessage msg = myAgent.receive(mt)
            if (msg != null) {
                System.out.println("Message received. Processing...")
                processAnswer(msg)
            }
            else {
                block()
            }
        }
    }

    def processAnswer(msg: ACLMessage) {
        AID speleologist = msg.getSender()
        if (msg.getContent().startsWith(Dictionary.POSSIBLE_OPTIONS)) {
            String options = msg.getContent().replace(Dictionary.POSSIBLE_OPTIONS, "")
            String[] numbers = options.split(Dictionary.DELIMITER)

            PathNode currentPosition = worldMap.get(speleologist)
            if (currentPosition == null){
                currentPosition = new PathNode(null)
                worldMap.put(speleologist, currentPosition)
            }

            if (currentPosition.childs.size() == numbers.length
                    || options.equals(Dictionary.NONE)) {
                PathNode backStep = currentPosition.parent
                if (backStep == null) {
                    sendAction(msg, Dictionary.STOP)
                } else {
                    sendBackAction(msg)
                }
            } else {
                sendForwardAction(msg)
            }
        }
    }

    def sendBackAction(msg: ACLMessage) {
        PathNode currentPosition = worldMap.get(msg.getSender())
        worldMap.replace(msg.getSender(), currentPosition.parent)
        String content = Dictionary.GO + Dictionary.BACK
        sendAction(msg, content)
    }

    def sendForwardAction(msg: ACLMessage) {
        PathNode currentPosition = worldMap.get(msg.getSender())
        PathNode nextStep = new PathNode(currentPosition)
        currentPosition.childs.add(nextStep)
        worldMap.replace(msg.getSender(), nextStep)
        String content = Dictionary.GO + currentPosition.childs.size()
        sendAction(msg, content)
    }

    def sendAction(msg: ACLMessage, content: String) {
        System.out.println("sending: " + content)
        ACLMessage reply = msg.createReply()
        reply.setPerformative(ACLMessage.PROPOSE)
        reply.setContent(content)
        send(reply)
    }
}
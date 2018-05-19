package ua.nure.agent

import akka.actor._
import ua.nure.world.Dictionary
import ua.nure.world.PathNode
import ua.nure.world.World
import java.util.List
import java.util.stream.Collectors
import java.util.stream.IntStream

import com.sun.deploy.util.StringUtils

class SpeleologistAgent extends Actor {

    private String TOPIC_ID = "navigator-speleologist_" + toString()

    private AID navigatorAgent

    private PathNode currentPosition
    
    protected void setup() {
        System.out.println("SpeleologistAgent " + getAID().getName() + " started")
        currentPosition = World.entrance
        addBehaviour(new ListenToNavigatorBehaviour())
    }

    private class ListenToNavigatorBehaviour extends CyclicBehaviour {
        val step = 0
        val mt: MessageTemplate

        def action() {
            switch (step) {
                case 0: {
                    findNavigator()
                    break
                }
                case 1: {
                    sendCurrentPosition()
                    break
                }
                case 2: {
                    receiveActionResponse()
                    break
                }
                case 3:
                    System.out.println("Job done!")
                    block()
            }
        }

        def findNavigator() {
            System.out.println("Trying to find navigator")
            DFAgentDescription dfd = new DFAgentDescription()
            ServiceDescription sd = new ServiceDescription()
            sd.setType(NavigatorAgent.NAVIGATOR_AGENT)
            dfd.addServices(sd)
            try {
                DFAgentDescription[] result = DFService.search(myAgent, dfd)
                if (result != null && result.length > 0) {
                    navigatorAgent = result[0].getName()
                    System.out.println("Navigator found " + navigatorAgent.getName())
                    ++step
                } else {
                    Thread.sleep(5000)
                }
            } catch (Exception e) {
                e.printStackTrace()
            }
        }

        def sendCurrentPosition() {
            ACLMessage message = new ACLMessage(ACLMessage.INFORM)
            message.addReceiver(navigatorAgent)
            message.setContent(positionToString())
            message.setConversationId(TOPIC_ID)
            message.setReplyWith("order" + System.currentTimeMillis())
            System.out.println(message.getContent() + " sent")
            myAgent.send(message)
            mt = MessageTemplate.and(MessageTemplate.MatchConversationId(TOPIC_ID),
                    MessageTemplate.MatchInReplyTo(message.getReplyWith()))
            ++step
        }

        def receiveActionResponse() {
            ACLMessage reply = myAgent.receive(mt)
            if (reply != null) {
                if (reply.getContent().startsWith(Dictionary.GO)) {
                    String action = reply.getContent().replace(Dictionary.GO, "")
                    if (Dictionary.BACK.equals(action)) {
                        System.out.println("going back")
                        currentPosition = currentPosition.parent
                    } else {
                        Integer pathNumber = Integer.parseInt(action)
                        System.out.println("going to " + pathNumber)
                        currentPosition = currentPosition.childs.get(pathNumber - 1)
                    }
                    --step
                }
                if (Dictionary.STOP.equals(reply.getContent())) {
                    System.out.println("stopping")
                    ++step
                }
            } else {
                block()
            }
        }
    }

    def positionToString() {
        if (currentPosition.childs.isEmpty()) {
            return Dictionary.POSSIBLE_OPTIONS + Dictionary.NONE
        }
        List[Integer] options = IntStream.rangeClosed(1, currentPosition.childs.size())
                .boxed().collect(Collectors.toList())
        return Dictionary.POSSIBLE_OPTIONS + StringUtils.join(options, Dictionary.DELIMITER)
    }
}
package gr.gnostix.freeswitch.actors

import akka.actor._
import gr.gnostix.freeswitch.actors.ActorsProtocol._

/**
 * Created by rebel on 7/8/15.
 */

/*
  object ChannelActor {
    def props(channelA: CallNew): Props = Props(new CallActor(channelA))
  }
*/

  class ChannelActor extends Actor with ActorLogging {
    var channelState: List[CallEventType] = List()

    def receive: Receive = {

      case x @ CallNew(uuid, eventName, fromUser, toUser, readCodec, writeCodec, fromUserIP, toUserIP, callUUID,
      callerChannelCreatedTime, callerChannelAnsweredTime, freeSWITCHHostname, freeSWITCHIPv4, callDirection, pdd, ringTimeSec) =>
        //channelState = channelState updated(uuid, x)
        channelState ::= x

        //log info s" channel actor state Map on CallNew $channelState"

      case x@CallEnd(uuid, eventName, fromUser, toUser, readCodec, writeCodec, fromUserIP, toUserIP, callUUID,
      callerChannelCreatedTime, callerChannelAnsweredTime, callerChannelHangupTime, freeSWITCHHostname,
      freeSWITCHIPv4, hangupCause, billSec, rtpQualityPerc, otherLegUniqueId, hangupDisposition, callDirection, mos,
      pdd, ringTimeSec) =>
        //context stop self
        // send message to parrent tha the channel is terminated
        channelState ::= x
        context.parent ! CallTerminated(x)
        //log info s" channel actor state List on CallEnd $channelState"

      case GetChannelInfo(callUuid, channeluuid) =>
        val response = channelState
        //log info s"channel response $response"
        sender ! response

      case x @ GetCallInfo(callUUID) =>
        //log info "channel actor sending his state"
        sender ! channelState.head

      case x @ GetConcurrentCallsChannel =>
        sender ! channelState.head

      case x @ GetCompletedCallsChannel =>
        //log info s"channel actor channels $channelState and sending ${channelState.head}"
        sender ! channelState.head

      case _ => log info s"message  not understood on channelActor"
    }
  }
package sc.protocol.room

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import sc.api.plugins.ITeam

/** Nachricht, die zu Beginn eines Spiels an einen Client geschickt wird, um ihm seine Spielerfarbe mitzuteilen.  */
@Suppress("DataClassPrivateConstructor")
@XStreamAlias(value = "welcomeMessage")
data class WelcomeMessage private constructor(
        @XStreamAsAttribute val color: String,
): RoomOrchestrationMessage {
    constructor(color: ITeam): this(color.name)
}
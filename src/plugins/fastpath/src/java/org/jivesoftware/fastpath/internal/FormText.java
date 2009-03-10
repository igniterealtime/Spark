/**
 * $RCSfile: ,v $
 * $Revision: $
 * $Date:  $
 *
 * Copyright (C) 1999-2008 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */
package org.jivesoftware.fastpath.internal;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.jivesoftware.smackx.workgroup.settings.ChatSetting;

public class FormText {

    public static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("MM/dd/yy h:mm");
    public static final SimpleDateFormat TIME_FORMATTER = new SimpleDateFormat("h:mm");

    public static String getChatRoomWelcomeMessage(String agent, String workgroup) {
        String value = getTextSetting("acceptedChat_text", workgroup);
        value = FormUtils.replace(value, "${agent}", agent);
        value = FormUtils.replace(value, "'", "\\\'");
        value = FormUtils.replace(value, "\n", "<br>");

        return value.trim();
    }

    public static String getWelcomeText(String workgroup) {
        return getTextSetting("user_input_page_title", workgroup);
    }

    public static String getChatDisconnected(String agent, String workgroup) {
        String value = getTextSetting("chatDisconnect_text", workgroup);
        value = FormUtils.replace(value, "${agent}", agent);
        return value;
    }

    public static String getSessionHasEnded(String agent, String workgroup) {
        String value = getTextSetting("chatSessionEnded_text", workgroup);
        value = FormUtils.replace(value, "${agent}", agent);
        return value;
    }

    public static String getInvitiationSent(String agent, String workgroup) {
        String value = getTextSetting("inviteChat_text", workgroup);
        value = FormUtils.replace(value, "${agent}", agent);
        return value;
    }

    public static String getTransferSent(String agent, String workgroup) {
        String value = getTextSetting("transferChat_text", workgroup);
        value = FormUtils.replace(value, "${agent}", agent);
        return value;
    }

    public static String getTranscriptText(String workgroup) {
        return getTextSetting("transcript_window_text", workgroup);
    }


    public static String getStartChatButtonText(String workgroup) {
        return getTextSetting("start_chat_button", workgroup);
    }

    public static String getQueueTitleText(String workgroup) {
        return getTextSetting("queue_title_text", workgroup);
    }

    public static String getQueueDescriptionText(String workgroup) {
        String value = getTextSetting("queue_description_text", workgroup);

        value = FormUtils.replace(value, "${position}", "<span id=\"queue_position\">?</span>");
        value = FormUtils.replace(value, "${waitTime}", "<span id=\"queue_time\">?</span>");
        return value;
    }

    public static String getTranscriptSent(String workgroup) {
        return getTextSetting("transcript_send_text", workgroup);
    }

    public static String getTranscriptNotSent(String workgroup) {
        return getTextSetting("transcript_not_sent_text", workgroup);
    }

    public static String agentHasEndedConversation(String agent, String workgroup) {
        String value = getTextSetting("chatSessionEnded_text", workgroup);
        value = FormUtils.replace(value, "${agent}", agent);
        value = FormUtils.replace(value, "\n", "<br>");

        return value;
    }

    public static String getStartButton(String workgroup) {
        return getTextSetting("start_chat_button", workgroup);
    }

    public static String getNoHelpText(String workgroup) {
        return getTextSetting("no_help_text", workgroup);
    }

    public static String getQueueFooter(String workgroup) {
        return getTextSetting("queue_footer_text", workgroup);
    }

    public static String getNoAgentText(String workgroup) {
        String value = getTextSetting("no_agent_text", workgroup);
        value = FormUtils.replace(value, "'", "\\\'");
        value = FormUtils.replace(value, "\n", "<br>");

        return value.trim();
    }

    public static String getInvitingAgentText(String workgroup) {
        return getTextSetting("inviteChat_text", workgroup);
    }

    public static String getTransferToAgentText(String workgroup) {
        return getTextSetting("transferChat_text", workgroup);
    }

    public static String getTextSetting(String key, String workgroup) {
        WorkgroupManager settingsManager = WorkgroupManager.getInstance();
        ChatSetting chatSettings = settingsManager.getChatSetting(key, workgroup);

        Date now = new Date();
        String date = DATE_FORMATTER.format(now);
        String time = TIME_FORMATTER.format(now);

        if (chatSettings == null) {
            return "";
        }

        String value = chatSettings.getValue();
        value = FormUtils.replace(value, "${time}", time);
        value = FormUtils.replace(value, "${date}", date);
        return value;
    }
}

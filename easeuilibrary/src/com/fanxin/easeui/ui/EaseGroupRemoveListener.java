package com.fanxin.easeui.ui;

import com.hyphenate.EMGroupChangeListener;

/**
 * group change listener
 *
 */
public abstract class EaseGroupRemoveListener implements EMGroupChangeListener{

    @Override
    public void onInvitationReceived(String groupId, String groupName, String inviter, String reason) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onInvitationAccepted(String groupId, String inviter, String reason) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onInvitationDeclined(String groupId, String invitee, String reason) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onAutoAcceptInvitationFromGroup(String groupId, String inviter, String inviteMessage) {
        // TODO Auto-generated method stub
    }
}

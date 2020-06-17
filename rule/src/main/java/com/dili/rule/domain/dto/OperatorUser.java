package com.dili.rule.domain.dto;

import com.dili.uap.sdk.domain.UserTicket;
import com.dili.uap.sdk.session.SessionContext;

public class OperatorUser {
    private Long userId;
    private String userName;

    public static OperatorUser build(SessionContext context) {
        UserTicket userTicket = context.getUserTicket();
        if(userTicket!=null){
            new OperatorUser(userTicket.getId(), userTicket.getRealName());
        }
        throw new IllegalArgumentException( "请先登录");
    }
    public static OperatorUser fromSessionContext() {
    	if(SessionContext.getSessionContext()!=null) {
            UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
            if(userTicket!=null){
                new OperatorUser(userTicket.getId(), userTicket.getRealName());
            }
    	}
        throw new IllegalArgumentException("请先登录");
    }


    /**
     * @param userId
     * @param userName
     */
    public OperatorUser(Long userId, String userName) {
        this.userId = userId;
        this.userName = userName;
    }

    /**
     * @return Long return the userId
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * @return String return the userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName the userName to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

}
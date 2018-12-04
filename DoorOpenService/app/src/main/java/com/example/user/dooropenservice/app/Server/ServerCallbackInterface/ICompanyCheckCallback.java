package com.example.user.dooropenservice.app.Server.ServerCallbackInterface;

import com.example.user.dooropenservice.app.Model.CompanyVO;

import java.util.ArrayList;

public interface ICompanyCheckCallback extends IServerCallback {
    void startService(ArrayList<CompanyVO> companyVOArrayList);
}

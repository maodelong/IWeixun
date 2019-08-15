package com.delong.factory.net;

import com.delong.factory.model.api.RspModel;
import com.delong.factory.model.api.account.AccountRsModel;
import com.delong.factory.model.api.account.LoginModel;
import com.delong.factory.model.api.account.RegisterModel;
import com.delong.factory.model.api.message.MsgCreateModel;
import com.delong.factory.model.api.user.UserUpdateModel;
import com.delong.factory.model.card.MessageCard;
import com.delong.factory.model.card.UserCard;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface RemoteService {

    @POST("account/register")
    Call<RspModel<AccountRsModel>> accountRegister(@Body RegisterModel model);

    @POST("account/login")
    Call<RspModel<AccountRsModel>> accountLogin(@Body LoginModel model);

    @POST("account/bind/{pushId}")
    Call<RspModel<AccountRsModel>> accountBind(@Path(encoded = true,value = "pushId")String pushId);

    @PUT("user")
    Call<RspModel<UserCard>> userUpdate(@Body UserUpdateModel model);

    @GET("user/search/{name}")
    Call<RspModel<List<UserCard>>> searchUser(@Path(value = "name",encoded = true) String name );

    @PUT("user/follow/{followId}")
    Call<RspModel<UserCard>> userFollow(@Path(value = "followId",encoded = true) String followId);

    @GET("user/contact")
    Call<RspModel<List<UserCard>>> userContacts();

    @GET("user/{id}")
    Call<RspModel<UserCard>> userFind(@Path(value = "id" ,encoded = true) String id);

    @POST("msg")
    Call<RspModel<MessageCard>> push(@Body MsgCreateModel model);

}

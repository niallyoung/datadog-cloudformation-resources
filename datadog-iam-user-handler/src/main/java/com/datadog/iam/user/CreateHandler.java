package com.datadog.iam.user;

import com.amazonaws.cloudformation.proxy.AmazonWebServicesClientProxy;
import com.amazonaws.cloudformation.proxy.Logger;
import com.amazonaws.cloudformation.proxy.ProgressEvent;
import com.amazonaws.cloudformation.proxy.OperationStatus;
import com.amazonaws.cloudformation.proxy.ResourceHandlerRequest;

import com.datadog.cloudformation.ApiClients;

import com.datadog.api.client.v1.ApiClient;
import com.datadog.api.client.v1.ApiException;
import com.datadog.api.client.v1.api.UsersApi;
import com.datadog.api.client.v1.model.User;
import com.datadog.api.client.v1.model.UserCreatePayload;
import com.datadog.api.client.v1.model.UserCreateResponse;

public class CreateHandler extends BaseHandler<CallbackContext> {

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final Logger logger) {
        final ResourceModel model = request.getDesiredResourceState();

        // TODO: how to add the client to BaseHandler, verify that credentials are ok, etc?
        // basically we're looking for something like provider setup method in TF
        ApiClient apiClient = ApiClients.V1Client();
        UsersApi usersApi = new UsersApi(apiClient);

        UserCreatePayload userCreatePayload = new UserCreatePayload()
            .accessRole(UserCreatePayload.AccessRoleEnum.fromValue(model.getAccessRole()))
            // TODO: not possible to set email when creating? .email(model.getEmail())
            .name(model.getName())
            .handle(model.getHandle());

        OperationStatus status = OperationStatus.SUCCESS;
        try {
            usersApi.createUser(userCreatePayload);
        } catch (ApiException e) {
            // TODO: how to return the exception text as a result?
            status = OperationStatus.FAILED;
            logger.log("Failed to create user: " + e.toString());
        }

        return ProgressEvent.<ResourceModel, CallbackContext>builder()
            .resourceModel(model)
            .status(status)
            .build();
    }
}

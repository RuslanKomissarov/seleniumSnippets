package com.utils;

import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.config.PropertiesManager;
import com.microsoft.graph.authentication.TokenCredentialAuthProvider;
import com.microsoft.graph.models.*;
import com.microsoft.graph.requests.AttachmentCollectionPage;
import com.microsoft.graph.requests.AttachmentCollectionResponse;
import com.microsoft.graph.requests.GraphServiceClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class GraphMailUtil {

    private final GraphServiceClient graphClient;

    private String CLIENTID;
    private String CLIENTSECRET;
    private String TENANTID;
    private List<String> SCOPES; //TODO do we need it as parameter or hardcode?
    private String SUBJECT; //TODO do we need it as parameter or hardcode?
    private String SENDEREMAIL;
    private String RECIPIENTMAIL;
    private PropertiesManager propertiesManager;

    public GraphMailUtil(){
        getConfiguration();
        this.graphClient = configureGraphClient();
    }

    private GraphServiceClient configureGraphClient(){
        ClientSecretCredential clientSecretCredential = initSecretCredential();
        TokenCredentialAuthProvider tokenCredentialAuthProvider = new TokenCredentialAuthProvider(SCOPES, clientSecretCredential);
        return GraphServiceClient.builder().authenticationProvider(tokenCredentialAuthProvider).buildClient();
    }

    private ClientSecretCredential initSecretCredential(){
        return new ClientSecretCredentialBuilder()
                .clientId(CLIENTID)
                .clientSecret(CLIENTSECRET)
                .tenantId(TENANTID)
                .build();
    }

    public void sendMail(String attachmentFileName) throws IOException {
        Message message = composeMessage(attachmentFileName);

        graphClient.users(SENDEREMAIL)
                .sendMail(UserSendMailParameterSet
                        .newBuilder()
                        .withMessage(message)
                        .withSaveToSentItems(null)
                        .build())
                .buildRequest()
                .post();
    }

    private Message composeMessage(String filename) throws IOException {
        Message message = new Message();

        message.subject = SUBJECT;
        message.body = new ItemBody();
        message.attachments = setAttachment(filename);

        LinkedList<Recipient> toRecipientsList = new LinkedList<>();
        Recipient toRecipient = new Recipient();
        EmailAddress emailAddress = new EmailAddress();
        emailAddress.address=RECIPIENTMAIL;
        toRecipient.emailAddress = emailAddress;
        toRecipientsList.add(toRecipient);
        message.toRecipients = toRecipientsList;

        return message;
    }

    private AttachmentCollectionPage setAttachment(String filename) throws IOException {
        FileAttachment pdfAttachment = new FileAttachment();
        pdfAttachment.name = filename;
        String filepath = "data/pdf/"+filename;
        Path path = Paths.get(filepath);
        pdfAttachment.contentBytes = Files.readAllBytes(path);
        pdfAttachment.oDataType = "#microsoft.graph.fileAttachment";

        LinkedList<Attachment> attachmentsList = new LinkedList<Attachment>();
        attachmentsList.add(pdfAttachment);
        AttachmentCollectionResponse response = new AttachmentCollectionResponse();
        response.value = attachmentsList;
        return new AttachmentCollectionPage(response, null);
    }

    private void getConfiguration() {
        propertiesManager = new PropertiesManager();
        try {
            CLIENTID = propertiesManager.setValueFromProperty("graph.client");
            CLIENTSECRET = propertiesManager.setValueFromProperty("graph.secret");
            TENANTID = propertiesManager.setValueFromProperty("graph.tenant");
            SENDEREMAIL = propertiesManager.setValueFromProperty("graph.sender");
            RECIPIENTMAIL = propertiesManager.setValueFromProperty("mail.recipient");
            SUBJECT = propertiesManager.setValueFromProperty("mail.subject");
            SCOPES = getScopes();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<String> getScopes() throws IOException {
        String scopes = propertiesManager.setValueFromProperty("graph.scopes");
        return Arrays.asList(scopes.split(","));
    }

}

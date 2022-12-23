package org.timeTable.CommunicationLayer;

public abstract class CommunicationService {

    //Each Communication Service needs to have its own typeID, communicationID, studentID
    //                                                for example whatsapp has typeID 3
    //                                                communicationID (The id specific to the student that the program then can lookup in the
    //                                                whatsapp table were all the relevant data is saved), for which student the data is saved
    
    CommunicationLayer communicationLayer;

    public CommunicationService(CommunicationLayer communicationLayer) {
        communicationLayer = communicationLayer.registerCommunicationService(this);

    }

    public CommunicationLayer getCommunicationLayer() {
        return communicationLayer;
    }

    public abstract void sendTimetableNews();

    public abstract void stopService();

}

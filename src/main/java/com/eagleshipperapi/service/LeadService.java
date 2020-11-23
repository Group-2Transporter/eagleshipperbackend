package com.eagleshipperapi.service;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;

import com.eagleshipperapi.bean.Bid;
import com.eagleshipperapi.bean.Lead;
import com.eagleshipperapi.bean.State;
import com.eagleshipperapi.bean.User;
import com.eagleshipperapi.exception.ResourceNotFoundException;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;

@Service
public class LeadService {
	
	private static final String TAG ="Lead";
	ArrayList<Lead> al = new ArrayList<Lead>();
	Firestore dbFirestore = FirestoreClient.getFirestore();
	
	//create lead 
	public Lead createNewLead(Lead lead) {
		String leadId = dbFirestore.collection(TAG).document().getId();
		lead.setLeadId(leadId);
		dbFirestore.collection(TAG).document(leadId).set(lead);
		return lead;
	}
	
	
	//get all lead by user id
		public ArrayList<Lead> getLeadByUserId(String userId) throws InterruptedException, ExecutionException{
			List<QueryDocumentSnapshot>document = dbFirestore.collection(TAG).whereEqualTo("userId", userId).get().get().getDocuments();
			for(QueryDocumentSnapshot queryDocument : document ) {
				al.add(queryDocument.toObject(Lead.class));
			}
			return al;		
		}
	//get single lead by id
		public Lead getLeadByLeadId(String leadId) throws InterruptedException, ExecutionException, ResourceNotFoundException {
			Lead lead =  dbFirestore.collection(TAG).document(leadId).get().get().toObject(Lead.class);
		    return lead;
		}
		
	//get Created Load by userId
		public ArrayList<Lead> getCreatedLeadById(String userId) throws Exception, Exception{
			ArrayList<Lead> al = new ArrayList<>();
			List<QueryDocumentSnapshot>document = dbFirestore.collection(TAG).whereEqualTo("userId", userId).get().get().getDocuments();
			for(QueryDocumentSnapshot queryDocument : document ) {
				Lead lead = queryDocument.toObject(Lead.class);
				if(lead.getStatus().equalsIgnoreCase("create"))
					al.add(lead);
			}
			return al;
		}
		
	//get Confirmed Load by userId	
		public ArrayList<Lead> getConfirmedLeadById(String userId) throws Exception, Exception{
			ArrayList<Lead> al = new ArrayList<>();
			List<QueryDocumentSnapshot>document = dbFirestore.collection(TAG).whereEqualTo("userId", userId).get().get().getDocuments();
			for(QueryDocumentSnapshot queryDocument : document ) {
				Lead lead = queryDocument.toObject(Lead.class);
				if(lead.getStatus().equalsIgnoreCase("confirmed"))
					al.add(lead);
			}
			return al;
		}
		
	//get Completed Load By userId	
		public ArrayList<Lead> getCompletedLeadById(String userId) throws Exception, Exception{
			ArrayList<Lead> al = new ArrayList<>();
			List<QueryDocumentSnapshot>document = dbFirestore.collection(TAG).whereEqualTo("userId", userId).get().get().getDocuments();
			for(QueryDocumentSnapshot queryDocument : document ) {
				Lead lead = queryDocument.toObject(Lead.class);
				if(lead.getStatus().equalsIgnoreCase("completed"))
					al.add(lead);
			}
			return al;
		}
		
	//get Completed load By TransporterId
		public ArrayList<Lead> getCompletedLeadByTransporterId(String transporterID) throws Exception, Exception{
			ArrayList<Lead> al = new ArrayList<>();
			List<QueryDocumentSnapshot>document = dbFirestore.collection(TAG).whereEqualTo("dealLockedWith", transporterID).get().get().getDocuments();
			for(QueryDocumentSnapshot queryDocument : document ) {
				Lead lead = queryDocument.toObject(Lead.class);
				if(lead.getStatus().equalsIgnoreCase("completed"))
					al.add(lead);
			}
			return al;
		}
	
	//get Confirm Lead Of All User match With Transporter Id Lead
		public ArrayList<Lead> getAllConfirmedLeadsOfUser(String transporterId) throws InterruptedException, ExecutionException{
			ArrayList<Lead> leadList = new ArrayList<>();
			List<QueryDocumentSnapshot> document = dbFirestore.collection(TAG).whereEqualTo("dealLockedWith", transporterId).get().get().getDocuments();
			for(QueryDocumentSnapshot ds : document) {
				Lead lead = ds.toObject(Lead.class);
				if(lead.getStatus().equalsIgnoreCase("confirmed"))
					leadList.add(lead);
			}
			return leadList;	
		}
		
		
	// update lead by LeadId
		public Lead updateLeadByLeadId(String leadId,Lead lead) throws InterruptedException, ExecutionException {
			Lead l = dbFirestore.collection(TAG).document(leadId).get().get().toObject(Lead.class);
			
			if(lead.getBidCount()!=0)
				l.setBidCount(lead.getBidCount());
			
			if(lead.getContactForDelivery()!=null)
				l.setContactForDelivery(lead.getContactForDelivery());
			
			if(lead.getContactForPickup()!=null)
				l.setContactForPickup(lead.getContactForPickup());
			
			if(lead.getDateOfCompletion()!=null)
				l.setDateOfCompletion(lead.getDateOfCompletion());
			
			if(lead.getDealLockedWith()!=null) {
				l.setDealLockedWith(lead.getDealLockedWith());
				l.setTransporterName(lead.getTransporterName());
			}
			
			if(lead.getDeliveryAddress()!=null)
				l.setDeliveryAddress(lead.getDeliveryAddress());
			
			if(lead.getPickUpAddress()!=null)
				l.setPickUpAddress(lead.getPickUpAddress());
			
			l.setStatus(lead.getStatus());
			
			if(lead.getTypeOfMaterial()!=null)
				l.setTypeOfMaterial(lead.getTypeOfMaterial());
			
			
			l.setMaterialStatus(lead.getMaterialStatus());
			
			l.setAmount(lead.getAmount());
			
			l.setWeight(lead.getWeight());
			
			l.setRemark(lead.getRemark());
			dbFirestore.collection(TAG).document(leadId).set(l);
			return l;
		}
		
		
		//get Filterd data
		public ArrayList<Lead> getFilteredLeads(String transporterId ,ArrayList<State> stateList) throws InterruptedException, ExecutionException{
			ArrayList<Lead> al = new ArrayList<>();
			boolean status = false;
			List<QueryDocumentSnapshot> query = dbFirestore.collection(TAG).get().get().getDocuments();
			for(State s : stateList) {
				for(QueryDocumentSnapshot ds : query) {
					Lead lead = ds.toObject(Lead.class);
					String[] pickup = lead.getPickUpAddress().split(",");
					if(pickup[2].equalsIgnoreCase(s.getStateList())) {
						if(lead.getStatus().equalsIgnoreCase("create")) {
							List<QueryDocumentSnapshot> bidQuery = dbFirestore.collection("Bid").whereEqualTo("leadId",lead.getLeadId()).get().get().getDocuments();
							for(QueryDocumentSnapshot bs : bidQuery) {
								Bid bid = bs.toObject(Bid.class);
								if(bid.getTransporterId().equals(transporterId)) {
									status = true;
									break;
								}
							}
							if(!status) {
								al.add(lead);
							}
							status = false;
						}
					}
				}
			}
			return al;
		}
		
		//get All created leads
		public ArrayList<Lead> getCreatedLeads(String transporterId) throws InterruptedException, ExecutionException{
		ArrayList<Lead> al = new ArrayList<>();
		boolean status = false;
		List<QueryDocumentSnapshot> query = dbFirestore.collection(TAG).whereEqualTo("status","create").get().get().getDocuments();
		for(QueryDocumentSnapshot ds : query) {
			Lead lead = ds.toObject(Lead.class);
			List<QueryDocumentSnapshot> bidQuery = dbFirestore.collection("Bid").whereEqualTo("leadId",lead.getLeadId()).get().get().getDocuments();
			for(QueryDocumentSnapshot bs : bidQuery) {
				Bid bid = bs.toObject(Bid.class);
				if(bid.getTransporterId().equals(transporterId)) {
					status = true;
					break;
				}
			}
			if(!status) {
				al.add(lead);
			}
			status = false;
		}
		return al;
	}
		

}

package com.eagleshipperapi.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.eagleshipperapi.FileUtility;
import com.eagleshipperapi.bean.Rating;
import com.eagleshipperapi.bean.Transporter;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.firebase.cloud.FirestoreClient;

@Service
public class TransporterService {

	private static final String TAG = "Transporter";
	
	
	//get all transporter
	public ArrayList<Transporter> getTransporter() throws InterruptedException, ExecutionException{
		Firestore fireStore = FirestoreClient.getFirestore();
		ArrayList<Transporter>transporterList = new ArrayList<Transporter>();
		List<QueryDocumentSnapshot> list = fireStore.collection(TAG).get().get().getDocuments();	
		for(QueryDocumentSnapshot queryDocument : list ) {
			transporterList.add(queryDocument.toObject(Transporter.class));
		}
		return transporterList;		
	}
	
	
	//get Single Transporter
	public Transporter getTransporter(String id) throws InterruptedException, ExecutionException {
		Firestore fireStore = FirestoreClient.getFirestore();
		Transporter t = fireStore.collection(TAG).document(id).get().get().toObject(Transporter.class);
		return t;
	}
	
	// create new transporter
	public Transporter createTransporter(Transporter transporter,MultipartFile file) throws IOException {
		Firestore fireStore = FirestoreClient.getFirestore();
		String imageUrl = new FileUtility().getImageUrl(file);
		transporter.setImageUrl(imageUrl);
		fireStore.collection(TAG).document(transporter.getTransporterId()).set(transporter);
		return transporter;
	}
	
	//delete Transporter by id
	public Transporter deleteTransporter(String id) throws InterruptedException, ExecutionException {
		Firestore fireStore = FirestoreClient.getFirestore();
		Transporter t = fireStore.collection(TAG).document(id).get().get().toObject(Transporter.class);
		fireStore.collection(TAG).document(id).delete();
		return t;
	}
	
	//update transporter details
	public Transporter updateTransporter(Transporter transporter) throws IOException, InterruptedException, ExecutionException {
		Firestore fireStore = FirestoreClient.getFirestore();
		fireStore.collection(TAG).document(transporter.getTransporterId()).set(transporter);
		Transporter t = fireStore.collection(TAG).document(transporter.getTransporterId()).get().get().toObject(Transporter.class);
		return t;
	}
	
	//update transporter image
	public Transporter updateTransporter(String transporterId,MultipartFile file) throws IOException, InterruptedException, ExecutionException {
		Firestore fireStore = FirestoreClient.getFirestore();
		Transporter t = fireStore.collection(TAG).document(transporterId).get().get().toObject(Transporter.class);
		String imageUrl =  new FileUtility().getImageUrl(file);
		t.setImageUrl(imageUrl);
		fireStore.collection(TAG).document(transporterId).set(t);
		return t;
	}
	
	//create rating
	public Rating createRating(String transporterId,String leadId,Rating rating) {
		Firestore fireStore = FirestoreClient.getFirestore();
		fireStore.collection("Rating").document(transporterId).collection("Rating").document(leadId).set(rating);
		return rating;
	}
	
	//get Rating by TransporterId
	public ArrayList<Rating> getTransporterRating(String transporterId) throws InterruptedException, ExecutionException{
		Firestore fireStore = FirestoreClient.getFirestore();
		ArrayList<Rating> ratingList = new ArrayList<>();
		List<QueryDocumentSnapshot> list = fireStore.collection("Rating").document(transporterId).collection("Rating").get().get().getDocuments();	
		for(QueryDocumentSnapshot queryDocument : list ) {
			ratingList.add(queryDocument.toObject(Rating.class));
		}
		return ratingList;
	}
	// get number of rating's and number of persons
	public ArrayList<Float> getNumberOfRating(String transporterId) throws InterruptedException, ExecutionException{
		Firestore fireStore = FirestoreClient.getFirestore();
		ArrayList<Float> al = new ArrayList<>();
		List<QueryDocumentSnapshot> list = fireStore.collection("Rating").document(transporterId).collection("Rating").get().get().getDocuments();	
		float totalRating = 0;
		float size = list.size();
		al.add(size);
		for(QueryDocumentSnapshot queryDocument : list ) {
			Rating rating = queryDocument.toObject(Rating.class);
			int r = Integer.parseInt(rating.getRating());
			totalRating+=r;
		}
		al.add(totalRating);
		return al;
	}
	
	
}
package com.eagleshipperapi.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.eagleshipperapi.bean.Rating;
import com.eagleshipperapi.bean.Transporter;
import com.eagleshipperapi.exception.ResourceNotFoundException;
import com.eagleshipperapi.service.TransporterService;

@RestController
@RequestMapping("/transporter")
public class TransporterController {

	@Autowired
	private TransporterService transporterService;
	
	//create Transporter 
	@PostMapping("/")
	public ResponseEntity<Transporter> createNewTransporter(@RequestParam("file")MultipartFile file,
			@RequestParam("transporterId") String transporterId,
			@RequestParam("type") String type,
			@RequestParam("name")String name,
			@RequestParam("contactNumber")String contactNumber,
			@RequestParam("address")String address, 
			@RequestParam("gstNumber")String gstNumber,
			@RequestParam("rating")String rating,
			@RequestParam("token")String token) throws ResourceNotFoundException, IOException {
		
		if(file.isEmpty())
			throw new ResourceNotFoundException("image not found.");
		Transporter transporter = new Transporter();
		transporter.setTransporterId(transporterId);
		transporter.setType(type);
		transporter.setName(name);
		transporter.setContactNumber(contactNumber);
		transporter.setAddress(address);
		if(gstNumber!=null) {
			transporter.setGstNumber(gstNumber);
		}
		transporter.setRating(rating); 
		transporter.setToken(token);
		return new ResponseEntity<Transporter>(transporterService.createTransporter(transporter,file),HttpStatus.OK);
	}
		
	//get All transporter
	@GetMapping("/")
	public ResponseEntity<ArrayList<Transporter>> getTransporter() throws InterruptedException, ExecutionException{
		return new ResponseEntity<ArrayList<Transporter>>(transporterService.getTransporter(),HttpStatus.OK);
	}
		
	//get Single Transporter by id
	@GetMapping("/{transporterId}")
	public ResponseEntity<Transporter> getTransporter(@PathVariable String transporterId) throws InterruptedException, ExecutionException, ResourceNotFoundException{
		Transporter t = transporterService.getTransporter(transporterId);
		if(t==null)
			throw new ResourceNotFoundException("Transporter not exists");
		return new ResponseEntity<Transporter>(t,HttpStatus.OK);
	}
		
	//delete Transporter by id
	@DeleteMapping("/{id}")
	public ResponseEntity<Transporter> deleteTransporter(@PathVariable String id) throws InterruptedException, ExecutionException, ResourceNotFoundException{
		Transporter t = transporterService.deleteTransporter(id);
		if(t==null)
			throw new ResourceNotFoundException("Transporter not exists");
		return new ResponseEntity<Transporter>(t,HttpStatus.OK);
	}
	
	//update Transporter 
	@PostMapping("/update")
	public ResponseEntity<Transporter> updateTransporter(@RequestBody Transporter transporter) throws ResourceNotFoundException, IOException, InterruptedException, ExecutionException {
		
		Transporter t = transporterService.updateTransporter(transporter);
		if(t==null)
			throw new ResourceNotFoundException("Transporter not exists");
		return new ResponseEntity<Transporter>(t,HttpStatus.OK);
	}
	
	//update Transporter image
	@PostMapping("/update/image")
	public ResponseEntity<Transporter> updateTransporter(@RequestParam("file")MultipartFile file,@RequestParam("transporterId") String transporterId) throws ResourceNotFoundException, IOException, InterruptedException, ExecutionException {
		
		Transporter t = transporterService.updateTransporter(transporterId, file);
		if(t==null)
			throw new ResourceNotFoundException("Transporter not exists");
		return new ResponseEntity<Transporter>(t,HttpStatus.OK);
	}
	
	//create Rating
	@PostMapping("/rating/{transporterId}/{leadId}")
	public ResponseEntity<Rating> createRating(@PathVariable String transporterId,@PathVariable String leadId,@RequestBody Rating rating) throws ResourceNotFoundException, IOException, InterruptedException, ExecutionException {
		return new ResponseEntity<Rating>(transporterService.createRating(transporterId, leadId, rating),HttpStatus.OK);
	}
	
	//Get Rating of Single Transporter
	@GetMapping("/rating/{transporterId}")
	public ResponseEntity<ArrayList<Rating>> createRating(@PathVariable String transporterId) throws ResourceNotFoundException, IOException, InterruptedException, ExecutionException {
		return new ResponseEntity<ArrayList<Rating>>(transporterService.getTransporterRating(transporterId),HttpStatus.OK);
	}
	
	// get number of rating's and number of persons
	@GetMapping("/rating/number/{transporterId}")
	public ResponseEntity<ArrayList<Float>> getNumberOfRating(@PathVariable String transporterId) throws ResourceNotFoundException, IOException, InterruptedException, ExecutionException {
		return new ResponseEntity<ArrayList<Float>>(transporterService.getNumberOfRating(transporterId),HttpStatus.OK);
	}
				

}
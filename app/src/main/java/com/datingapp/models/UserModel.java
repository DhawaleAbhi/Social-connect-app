package com.datingapp.models;

import java.io.Serializable;
import java.util.Date;

public class UserModel implements Serializable {
 private String name;
 private String gender;
 private String honest;
 private String interest;
 private String working;
 private String email;
 private String phoneNumber;
 private Date created_by;
 private Date updated_to;
 private String marital_status;
 private String date_of_birth;
 private String url;

 public Date getCreated_by() {
  return created_by;
 }

 public void setCreated_by(Date created_by) {
  this.created_by = created_by;
 }

 public Date getUpdated_to() {
  return updated_to;
 }

 public void setUpdated_to(Date updated_to) {
  this.updated_to = updated_to;
 }

 public String getName() {
  return name;
 }

 public void setName(String name) {
  this.name = name;
 }

 public String getGender() {
  return gender;
 }

 public void setGender(String gender) {
  this.gender = gender;
 }

 public String getHonest() {
  return honest;
 }

 public void setHonest(String honest) {
  this.honest = honest;
 }

 public String getInterest() {
  return interest;
 }

 public void setInterest(String interest) {
  this.interest = interest;
 }

 public String getWorking() {
  return working;
 }

 public void setWorking(String working) {
  this.working = working;
 }

 public String getEmail() {
  return email;
 }

 public void setEmail(String email) {
  this.email = email;
 }

 public String getPhoneNumber() {
  return phoneNumber;
 }

 public void setPhoneNumber(String phoneNumber) {
  this.phoneNumber = phoneNumber;
 }

 public String getMarital_status() {
  return marital_status;
 }

 public void setMarital_status(String marital_status) {
  this.marital_status = marital_status;
 }

 public String getDate_of_birth() {
  return date_of_birth;
 }

 public void setDate_of_birth(String date_of_birth) {
  this.date_of_birth = date_of_birth;
 }

 public String getUrl() {
  return url;
 }

 public void setUrl(String url) {
  this.url = url;
 }
}

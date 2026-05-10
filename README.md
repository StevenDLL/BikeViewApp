Information about our project here...

## Demo Security Notes

This project can use a local Firebase Admin service-account JSON for class/demo runs. Keep those files under `config/` and never commit them. A production desktop release should move Firebase access behind a backend API, or use Firebase client authentication with Firestore security rules.

For demo credentials, use least-privilege access limited to the app's station sync data. The intended write path is `FirestoreSeeder`, which writes `stations/{stationId}` and `app_metadata/status` from the live GBFS station feed.

Launch<br>
<img width="377" height="587" alt="Launch" src="https://github.com/user-attachments/assets/817c7d36-7068-4fb5-bc2d-e7c53a80c3d4" />

MapView<br>
<img width="1282" height="752" alt="MapView" src="https://github.com/user-attachments/assets/0c35e113-1d9d-4ce3-9552-0376de708d29" />

Interactive Hover and Info Panel<br>
<img width="1282" height="752" alt="Station_Info_View_And_Hover" src="https://github.com/user-attachments/assets/b05b90ec-4db6-46cd-afa3-2f5e50043288" />

Filter By Zipcode<br>
<img width="1282" height="752" alt="Filter_By_Zipcode" src="https://github.com/user-attachments/assets/cb7fa8ad-1bb4-475f-b44b-49d892f7a44d" />

Filter By Borough<br>
<img width="1282" height="752" alt="Filter_By_Borough" src="https://github.com/user-attachments/assets/91ce9b08-c0f6-4a51-af76-12410c261173" />

Filter By Station<br>
<img width="1282" height="752" alt="Filter_By_Station" src="https://github.com/user-attachments/assets/6d42502c-6eb9-4b09-a0c0-9eeb4a267e68" />

Mix & Match Filters<br>
<img width="1282" height="752" alt="Filter_By_Multiple" src="https://github.com/user-attachments/assets/3a20201c-d8bc-421b-a8f8-609d3bd9c6fc" />



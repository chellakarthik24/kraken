import {Component} from '@angular/core';
import {of} from 'rxjs';
import {WindowService} from 'projects/tools/src/lib/window.service';
import {HttpClient} from '@angular/common/http';
import {map} from 'rxjs/operators';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  constructor(private http: HttpClient,
              private window: WindowService) {
  }

  public open() {
    const login = this.http.post('http://localhost:8300/grafana/login', {'user': 'admin', 'password': 'kraken', 'email': ''});
    this.window.open(login.pipe(map(value => 'http://localhost:8300/grafana/')));
  }
}

// var xhr = new XMLHttpRequest();
// xhr.open("POST", 'http://localhost:8300/grafana/login', true);
//
// //Send the proper header information along with the request
// xhr.setRequestHeader("Content-Type", "application/json");
//
// xhr.onreadystatechange = function() { // Call a function when the state changes.
//   if (this.readyState === XMLHttpRequest.DONE && this.status === 200) {
//     // Request finished. Do processing here.
//     window.open("http://localhost:8300/grafana/");
//   }
// }
// xhr.send('{"user":"admin","password":"kraken","email":""}');

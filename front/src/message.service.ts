import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
// @ts-ignore
import { Observable } from 'rxjs/Observable';

@Injectable()
export class CarService {

  constructor(private http: HttpClient) {
  }

  getAll(): Observable<any> {
    return this.http.get('//localhost:8080/main');
  }
}

/* tslint:disable */
/* eslint-disable */
/* Code generated by ng-openapi-gen DO NOT EDIT. */

import { HttpClient, HttpContext, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';

import { WeatherSensorRequest } from '../../models/weather-sensor-request';
import { WeatherSensorResponse } from '../../models/weather-sensor-response';

export interface AddSensor$Params {
      body: WeatherSensorRequest
}

export function addSensor(http: HttpClient, rootUrl: string, params: AddSensor$Params, context?: HttpContext): Observable<StrictHttpResponse<WeatherSensorResponse>> {
  const rb = new RequestBuilder(rootUrl, addSensor.PATH, 'post');
  if (params) {
    rb.body(params.body, 'application/json');
  }

  return http.request(
    rb.build({ responseType: 'json', accept: 'application/json', context })
  ).pipe(
    filter((r: any): r is HttpResponse<any> => r instanceof HttpResponse),
    map((r: HttpResponse<any>) => {
      return r as StrictHttpResponse<WeatherSensorResponse>;
    })
  );
}

addSensor.PATH = '/weatherSensor';

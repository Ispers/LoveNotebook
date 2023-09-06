import request from './request'

export function httpGet(url, params) {
  return new Promise((resolve, reject)=>{
      request.get(url, params).then((result)=>{
          resolve(result)
      }).catch(err=>{
          reject(err)
      });
  });
}

export function httpPost(url, params) {
  return new Promise((resolve, reject)=>{
      request.post(url, params).then((result)=>{
          resolve(result)
      }).catch(err=>{
          reject(err)
      });
  });
}
export function httpGetByToken(url, params) {
  return new Promise((resolve, reject)=>{
      request.getByToken(url, params).then((result)=>{
          resolve(result)
      }).catch(err=>{
          reject(err)
      });
  });
}

export function httpPostByToken(url, params) {
  return new Promise((resolve, reject)=>{
      request.postByToken(url, params).then((result)=>{
          resolve(result)
      }).catch(err=>{
          reject(err)
      });
  });
}

// Executar somente contra um banco H2 em memória novo, conforme STUDIO.md.
const fs = require('fs');
const vm = require('vm');
const assert = require('node:assert/strict');
const TEST_URL=process.env.GAMESRUN_TEST_URL || 'http://127.0.0.1:18081';
let sessionCookie='';
const nodes = new Map();
class Element {
  constructor() {this.innerHTML='';this.textContent='';this.value='';this.hidden=false;this.disabled=false;this.children=[];this.dataset={};this.attrs={};this.open=false;this.isConnected=true;this.fields={};this.type='password';}
  querySelector(selector) {return node(selector);}
  querySelectorAll() {return [];}
  addEventListener() {}
  setAttribute(name,value) {this.attrs[name]=value;}
  getAttribute(name) {return this.attrs[name];}
  removeAttribute(name) {delete this.attrs[name];}
  appendChild(el) {this.children.push(el);el.parentElement=this;}
  get firstElementChild() {return this.children[0];}
  remove() {if(this.parentElement)this.parentElement.children=this.parentElement.children.filter(el=>el!==this);}
  focus() {}
  scrollIntoView() {}
  showModal() {this.open=true;}
  close() {this.open=false;}
  reset() {this.fields={};}
}
function node(selector){if(!nodes.has(selector))nodes.set(selector,new Element());return nodes.get(selector);}
function storage(){const data=new Map();return {getItem:k=>data.get(k)||null,setItem:(k,v)=>data.set(k,String(v)),removeItem:k=>data.delete(k)};}
let queue=[],calls=[];
const context=vm.createContext({console,URL,AbortController,localStorage:storage(),sessionStorage:storage(),location:{protocol:'http:',hash:'#home'},window:{GAMESRUN_CONFIG:{timeoutMs:100},addEventListener(){},scrollTo(){}},document:{querySelector:node,querySelectorAll(){return [];},createElement(){return new Element();},getElementById:id=>node('#'+id),addEventListener(){},activeElement:node('opener')},setTimeout:()=>1,clearTimeout(){},requestAnimationFrame:fn=>fn(),FormData:class extends FormData{constructor(form){super();if(form&&!(form instanceof Element))throw new TypeError('FormData precisa de um formulário');for(const [key,value] of Object.entries(form?.fields||{}))this.append(key,value);}},fetch:async(url,options)=>{calls.push({url,options});const response=await fetch(TEST_URL+url,{...options,headers:{...options.headers,...(sessionCookie?{Cookie:sessionCookie}:{})}});const cookie=response.headers.get('set-cookie');if(cookie)sessionCookie=cookie.split(';')[0];return response;}});
const source=fs.readFileSync(require('node:path').join(__dirname,'../src/main/resources/static/script.js'),'utf8').replace(/initApp\(\);\s*$/,'');
vm.runInContext(source,context);
const run=code=>vm.runInContext(code,context);
function respond(path,body,status=200){queue.push({path,body,status});}
function resetCalls(){calls=[];queue=[];}
function event(selector){return {preventDefault(){},target:node(selector),currentTarget:context.document};}
async function test(name,fn){await fn();console.log('PASS '+name);}
(async()=>{
 await test('Spring Boot serves the Studio homepage and local assets',async()=>{
  const page=await fetch(TEST_URL+'/');assert.equal(page.status,200);assert.match(await page.text(),/signupIdade/);
  for(const asset of ['style.css','script.js','config.js','assets/ps5.svg'])assert.equal((await fetch(TEST_URL+'/'+asset)).status,200);
 });
 await test('frontend loads real baseline categories and an empty catalog',async()=>{await run('initApp()');assert.equal(run('state.demo'),false);assert.equal(run('state.products.length'),0);assert.equal(run('state.categories.length'),5);assert.equal(run('state.connectionError'),'');});
 const token=Date.now().toString();
 await test('frontend creates a real account with age and CPF',async()=>{
  node('#signupNome').value='Studio Smoke '+token;node('#signupEmail').value='studio'+token+'@example.com';node('#signupPassword').value='studio12345';node('#signupCpf').value='529.982.247-25';node('#signupIdade').value='18';context.signupEvent=event('#signupForm');
  await run('handleAuth(signupEvent,true)');assert.ok(run('state.user?.id'));assert.equal(run('state.user.nome'),'Studio Smoke '+token);assert.equal(node('#signupError').textContent,'');
 });
 await test('frontend publishes a real announcement with backend category IDs',async()=>{
  node('#announcementForm').fields={produto:'Controle Studio '+token,preco:'250.00',descricao:'Controle em perfeito estado, com cabo e caixa original.',categoriaId:String(run('state.categories[0].id')),estadoConservacao:'Usado - Como Novo',imagemUrl:''};context.localPhoto=new Blob([Buffer.from('iVBORw0KGgoAAAANSUhEUgAAAAIAAAACCAIAAAD91JpzAAAAEElEQVR4nGP4H8oARAwQCgApOgVR47YLaAAAAABJRU5ErkJggg==','base64')],{type:'image/png'});Object.defineProperty(context.localPhoto,'name',{value:'foto-local.png'});run('chooseSellerImage(localPhoto)');context.listingEvent=event('#announcementForm');await run('handleCreateAnnouncement(listingEvent)');assert.equal(run('state.products.length'),1);assert.equal(node('#announcementError').textContent,'');const url=run('state.products[0].imagemUrl');assert.match(url,/^\/uploads\/anuncios\//);const photo=await fetch(TEST_URL+url);assert.equal(photo.status,200);assert.equal(photo.headers.get('content-type'),'image/png');
 });
 const product=run('state.products[0].id');context.productId=product;
 await test('favorite is saved in the server account without localStorage',async()=>{await run('toggleFavorite(productId)');assert.equal(run('state.favorites.join()'),String(product));assert.equal(context.localStorage.getItem('gamesrun_live_v1_favorites_v2'),null);});
 await test('guest basket requires login before creating an order',async()=>{
  await run('logout()');assert.equal(run('state.favorites.length'),0);await run('addToCart(productId)');await run('addToCart(productId)');assert.equal(run('state.cart[0].quantidade'),2);
  node('#checkoutForm').fields={payment:'pix'};node('#checkoutAddress').value='Rua Teste, 123 - Centro - São Paulo/SP';context.checkoutEvent=event('#checkoutForm');const count=calls.length;await run('executeCheckout(checkoutEvent)');assert.equal(calls.length,count);assert.equal(node('#authModal').open,true);
 });
 await test('login transfers guest quantities to the real account once',async()=>{
  node('#loginEmail').value='studio'+token+'@example.com';node('#loginPassword').value='studio12345';context.loginEvent=event('#loginForm');await run('handleAuth(loginEvent,false)');assert.ok(run('state.user?.id'));assert.equal(run('state.cart[0].quantidade'),2);await run('refreshAccount(true)');assert.equal(run('state.cart[0].quantidade'),2);assert.equal(run('state.cartSyncError'),'');assert.equal(run('state.favorites.join()'),String(product));
 });
 await test('cart quantity changes persist through the backend',async()=>{await run('changeQuantity(productId,1)');assert.equal(run('state.cart[0].quantidade'),3);await run('refreshAccount()');assert.equal(run('state.cart[0].quantidade'),3);await run('changeQuantity(productId,-1)');assert.equal(run('state.cart[0].quantidade'),2);});
 for(const method of ['pix','cartao','boleto']){
  await test('frontend completes '+method+' order and clears the real cart',async()=>{
   if(!run('state.cart.length'))await run('addToCart(productId)');
   node('#checkoutForm').fields={payment:method};node('#checkoutAddress').value='Rua Teste, 123 - Centro - São Paulo/SP';const expected=run('subtotal()+25');
   await run('executeCheckout(checkoutEvent)');assert.equal(node('#checkoutError').textContent,'');assert.equal(run('state.cart.length'),0);assert.equal(run('state.orders[0].status'),'AGUARDANDO_PAGAMENTO');assert.equal(run('state.orders[0].formaPagamento'),method);assert.equal(run('state.orders[0].total'),expected);
   const remote=await(await fetch(TEST_URL+'/api/carrinhos/usuario/'+run('state.user.id'))).json();assert.equal(remote.length,0);
   const orderId=run('state.orders[0].id');context.currentOrderId=orderId;run("state.view='pagamento'");await run('loadPayment(currentOrderId)');assert.equal(run('state.payment.simulado'),true);assert.ok(run('state.payment.codigo').startsWith('GAMESRUN-'));
   if(method==='pix'){assert.match(run('state.payment.qrSvg'),/<svg/);const old=run('state.payment.codigo');await run('loadPayment(currentOrderId)');assert.notEqual(run('state.payment.codigo'),old);}
   if(method==='cartao'){node('#testCardName').value='PLAYER TESTE';node('#testCardNumber').value='4242 4242 4242 4242';node('#testCardExpiry').value='12/'+String((new Date().getFullYear()+2)%100);node('#testCardCvv').value='123';}
   if(method==='boleto')assert.match(run('state.payment.barrasSvg'),/<svg/);
   context.simulationEvent=event('#simulationPaymentForm');await run('confirmSimulation(simulationEvent)');assert.equal(run('state.orders[0].status'),'PAGAMENTO_SIMULADO');assert.equal(node('#simulationPaymentError').textContent,'');

  });
 }
 await test('all three real orders appear in account history',async()=>{await run('loadOrders()');assert.equal(run('state.orders.length'),3);assert.equal(run('state.ordersError'),'');});

 await test('another account has independent favorites and cannot remove the first account favorite',async()=>{
  await run('logout()');node('#signupNome').value='Segundo Studio '+token;node('#signupEmail').value='outro'+token+'@example.com';node('#signupCpf').value='111.444.777-35';node('#signupIdade').value='18';node('#signupPassword').value='studio12345';
  await run('handleAuth(signupEvent,true)');assert.equal(node('#signupError').textContent,'');assert.equal(run('state.favorites.length'),0);
  await run('toggleFavorite(productId)');assert.equal(run('state.favorites.join()'),String(product));await run('toggleFavorite(productId)');assert.equal(run('state.favorites.length'),0);
  await run('logout()');node('#loginEmail').value='studio'+token+'@example.com';node('#loginPassword').value='studio12345';await run('handleAuth(loginEvent,false)');assert.equal(run('state.favorites.join()'),String(product));
 });
 await test('seller deletes its listing from the database through the frontend',async()=>{context.window.confirm=()=>true;await run('deleteListing(productId)');assert.equal((await fetch(TEST_URL+'/api/anuncios/listar/'+product)).status,404);});
 console.log('PASS real HTTP flow: '+calls.length+' frontend API requests; real Spring Boot + isolated H2 memory database.');
})().catch(error=>{console.error(error);process.exit(1);});

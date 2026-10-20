// Verificações de lógica com DOM e API simulados; não substituem testes visuais.
const fs = require('fs');
const vm = require('vm');
const assert = require('node:assert/strict');
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
const context=vm.createContext({console,URL,AbortController,localStorage:storage(),sessionStorage:storage(),location:{protocol:'http:',hash:'#home'},window:{GAMESRUN_CONFIG:{timeoutMs:100},addEventListener(){},scrollTo(){}},document:{querySelector:node,querySelectorAll(){return [];},createElement(){return new Element();},getElementById:id=>node('#'+id),addEventListener(){},activeElement:node('opener')},setTimeout:()=>1,clearTimeout(){},requestAnimationFrame:fn=>fn(),FormData:class extends FormData{constructor(form){super();if(form&&!(form instanceof Element))throw new TypeError('FormData precisa de um formulário');for(const [key,value] of Object.entries(form?.fields||{}))this.append(key,value);}},fetch:async(url,options)=>{calls.push({url,options});const next=queue.shift();if(!next)throw new TypeError('Offline');if(next.error)throw next.error;if(next.wait)await next.wait;assert.equal(url,next.path);return {ok:(next.status||200)<400,status:next.status||200,text:async()=>JSON.stringify(next.body)};}});
const source=fs.readFileSync(require('node:path').join(__dirname,'../src/main/resources/static/script.js'),'utf8').replace(/initApp\(\);\s*$/,'');
context.localStorage.setItem('gamesrun_live_v1_favorites_v2',JSON.stringify(['old-shared-product']));
vm.runInContext(source,context);
const run=code=>vm.runInContext(code,context);
function respond(path,body,status=200){queue.push({path,body,status});}
function resetCalls(){calls=[];queue=[];}
function event(selector){return {preventDefault(){},target:node(selector),currentTarget:context.document};}
async function test(name,fn){await fn();console.log('PASS '+name);}
(async()=>{
 await test('offline catalog shows retry rather than fictitious products',async()=>{await run('initApp()');assert.equal(run('state.products.length'),0);assert.match(node('#app').innerHTML,/Recarregar catálogo/);});
 await test('valid empty API catalog stays empty',async()=>{resetCalls();respond('/api/categorias/listar',[]);respond('/api/anuncios/listar',[]);respond('/api/usuarios/sessao',{message:'Entre'},401);await run('initApp()');assert.equal(run('state.demo'),false);assert.equal(run('state.products.length'),0);assert.match(node('#productGrid').innerHTML,/primeiro achado/);});
 run('state.products=[...demoProducts];state.categories=mockCategories;state.demo=true;state.loading=false;');
 await test('combined search, condition, price and sorting',async()=>{run("state.filters={query:'nintendo',category:'',platform:'',condition:'usado',price:'1500',favorites:false,sort:'price-asc'}");assert.equal(run('filteredProducts().length'),2);assert.equal(run('filteredProducts()[0].id'),'demo-4');run("state.filters.condition='novo'");assert.equal(run('filteredProducts().length'),0);run("state.filters={query:'',category:'',platform:'',condition:'novo',price:'',favorites:false,sort:'price-asc'}");assert.equal(run('filteredProducts().length'),2);assert.equal(run('filteredProducts()[0].id'),'demo-6');});
 await test('favorites require login instead of saving a guest list',async()=>{resetCalls();run("state.user=null;state.favorites=[]");await run("toggleFavorite('demo-2')");assert.equal(context.localStorage.getItem('gamesrun_live_v1_favorites_v2'),null);assert.equal(calls.length,0);assert.equal(run('state.favorites.length'),0);assert.equal(node('#authModal').open,true);run('state.afterAuth=null');});
 await test('favorites save to the API and update the filter only on success',async()=>{resetCalls();run("state.user={id:9,nome:'Player'};state.products=[{id:10,nome:'Real',preco:100,demo:false}];state.favoritesReady=true;state.filters={query:'',category:'',platform:'',condition:'',price:'',favorites:true,sort:'recent'}");respond('/api/favoritos/10',null,204);await run('toggleFavorite(10)');assert.equal(calls[0].options.method,'PUT');assert.equal(run('filteredProducts().length'),1);assert.equal(context.localStorage.getItem('gamesrun_live_v1_favorites_v2'),null);respond('/api/favoritos/10',{message:'Falha'},503);await run('toggleFavorite(10)');assert.equal(run('state.favorites.length'),1);respond('/api/favoritos/10',null,204);await run('toggleFavorite(10)');assert.equal(calls[2].options.method,'DELETE');assert.equal(run('state.favorites.length'),0);});
 await test('guest cart quantities and removal persist',async()=>{run('state.cart=[];state.user=null;state.products=[...demoProducts]');await run("addToCart('demo-1')");await run("addToCart('demo-1')");assert.equal(run('state.cart[0].quantidade'),2);await run("changeQuantity('demo-1',-1)");assert.equal(run('state.cart[0].quantidade'),1);await run("changeQuantity('demo-1',0,true)");assert.equal(run('state.cart.length'),0);});
 await test('failed API mutation keeps account cart unchanged',async()=>{resetCalls();run("state.user={id:9,nome:'Player'};state.products=[{id:10,nome:'Real',preco:100,demo:false}];state.cart=[]");respond('/api/carrinhos/usuario/9/itens',{message:'Sem estoque'},409);assert.equal(await run('addToCart(10)'),false);assert.equal(run('state.cart.length'),0);assert.equal(run('state.cartBusy'),false);});
 await test('login error never creates local user',async()=>{resetCalls();run('state.user=null');node('#loginEmail').value='teste@example.com';node('#loginPassword').value='example-password';respond('/api/usuarios/login',{message:'Credenciais inválidas'},401);context.loginEvent=event('#loginForm');await run('handleAuth(loginEvent,false)');assert.equal(run('state.user'),null);assert.equal(node('#loginError').textContent,'Credenciais inválidas');});
 await test('login uses original payload, stores no password, loads account',async()=>{resetCalls();run('state.demo=false;state.products=[{id:10,nome:"Real",preco:100,demo:false}];state.cart=[]');respond('/api/usuarios/login',{id:9,nome:'Player Teste',nick:'teste@example.com',senha:'must-not-store',cpf:'must-not-store'});respond('/api/favoritos',[]);respond('/api/carrinhos/usuario/9',[]);respond('/api/pedidos/comprador/9',[]);await run('handleAuth(loginEvent,false)');assert.equal(run('state.user.id'),9);assert.equal(node('#loginError').textContent,'');assert.equal(JSON.parse(calls[0].options.body).nick,'teste@example.com');assert.ok(!context.sessionStorage.getItem('gamesrun_live_v1_session_v2').includes('must-not-store'));});
 await test('invalid CPF blocks signup before API',async()=>{resetCalls();node('#signupIdade').value='18';node('#signupNome').value='Player Teste';node('#signupEmail').value='teste@example.com';node('#signupPassword').value='example-password';node('#signupCpf').value='111.111.111-11';context.signupEvent=event('#signupForm');await run('handleAuth(signupEvent,true)');assert.equal(calls.length,0);assert.match(node('#signupError').textContent,/CPF/);assert.equal(run("validCPF('529.982.247-25')"),true);});
 await test('checkout demo items cannot create a real order',async()=>{resetCalls();run('state.cart=[{...demoProducts[0],quantidade:1}]');context.checkoutEvent=event('#checkoutForm');await run('executeCheckout(checkoutEvent)');assert.equal(calls.length,0);assert.equal(run('state.cart.length'),1);});
 await test('failed order preserves cart and restores button',async()=>{resetCalls();run("state.cart=[{id:10,nome:'Real',preco:100,demo:false,quantidade:1}];state.orders=[]");node('#checkoutForm').fields={payment:'pix'};node('#checkoutAddress').value='Rua de exemplo, 100, São Paulo, SP';respond('/api/pedidos/cadastrar',{message:'Pedido recusado'},400);await run('executeCheckout(checkoutEvent)');assert.equal(run('state.cart.length'),1);assert.equal(run('state.orders.length'),0);assert.equal(run('state.checkoutBusy'),false);});
 await test('successful order remains successful if cart cleanup fails',async()=>{resetCalls();respond('/api/pedidos/cadastrar',{id:42,status:'AGUARDANDO_PAGAMENTO'});respond('/api/carrinhos/usuario/9/itens/10',{message:'Unavailable'},503);await run('executeCheckout(checkoutEvent)');assert.equal(run('state.cart.length'),0);assert.equal(run('state.orders[0].id'),42);assert.equal(context.location.hash,'#pagamento/42');const order=JSON.parse(calls[0].options.body);assert.equal(order.status,'AGUARDANDO_PAGAMENTO');assert.equal(order.valorTotal,125);assert.equal(order.formaPagamento,'pix');assert.equal(JSON.parse(order.itensResumo)[0].quantidade,1);});
 await test('untrusted text and image protocols are escaped',async()=>{const card=run("productCard({id:'1\" onclick=\"x',nome:'<img src=x onerror=alert(1)>',preco:1,categoria:{nome:'<script>'},estadoConservacao:'Novo',vendedor:{nome:'<script>'},imagemUrl:'javascript:alert(1)'})");assert.ok(!card.includes('<img src=x'));assert.ok(card.includes('&lt;img'));assert.ok(!card.includes('src="javascript:'));assert.equal(run("safeImage('data:text/html,unsafe')"),'assets/controller.svg');});
 await test('all routes render and unknown product has empty state',async()=>{const output={};run('state.products=[...demoProducts];state.categories=mockCategories;state.cart=[{...demoProducts[0],quantidade:1}];state.user={id:9,nome:"Player"};state.ordersError="";state.orders=[]');for(const route of ['home','produto/demo-1','carrinho','checkout','vendedor','pedidos','chat','produto/not-found']){context.location.hash='#'+route;run('renderRoute()');output[route]=node('#app').innerHTML;assert.ok(output[route].length>100);}});

 await test('guest merge retry uses its original target quantity after lost response',async()=>{
   resetCalls();run("state.user={id:9,nome:'Player'};state.products=[{id:10,nome:'Real',preco:100,demo:false}];state.cart=[];state.cartSyncError='';localStorage.setItem('gamesrun_live_v1_cart_v2_guest',JSON.stringify([{id:10,nome:'Real',preco:100,quantidade:2}]));");
   respond('/api/carrinhos/usuario/9',[]);queue.push({path:'/api/carrinhos/usuario/9/itens',error:new TypeError('Connection lost')});respond('/api/pedidos/comprador/9',[]);
   assert.equal(await run('refreshAccount(true)'),false);assert.ok(run('state.cartSyncError'));
   resetCalls();respond('/api/carrinhos/usuario/9',[{anuncioId:10,quantidade:2}]);respond('/api/pedidos/comprador/9',[]);
   assert.equal(await run('refreshAccount(true)'),true);assert.equal(run('state.cart[0].quantidade'),2);assert.equal(calls.filter(c=>c.options.method==='POST'||c.options.method==='PUT').length,0);assert.equal(run("JSON.parse(localStorage.getItem('gamesrun_live_v1_cart_v2_guest')).length"),0);
 });

 await test('announcement submit uses target form with a document-level listener',async()=>{
  resetCalls();run("state.user={id:9,nome:'Player'};state.demo=false;state.categoriesReady=true;state.products=[]");
  node('#announcementForm').fields={produto:'Anúncio de teste',preco:'150',categoriaId:'1',estadoConservacao:'Usado - Bom Estado',imagemUrl:'http://localhost:8081/assets/ps5.svg',descricao:'Produto de teste em bom estado e funcionando.'};
  respond('/api/anuncios/cadastrar',{id:99,produto:'Anúncio de teste',preco:150,categoriaId:1,vendedorId:9});context.announcementEvent=event('#announcementForm');
  await run('handleCreateAnnouncement(announcementEvent)');assert.equal(node('#announcementError').textContent,'');assert.equal(run('state.products[0].id'),99);
  assert.equal(JSON.parse(calls[0].options.body).vendedorId,9);
 });

 await test('favorites keep the red heart after a successful server save',async()=>{
  resetCalls();run("state.user={id:9,nome:'Player'};state.products=[{...demoProducts[0],id:10,demo:false}];clearFavorites();state.favoritesReady=true;state.filters={query:'',category:'',platform:'',condition:'',price:'',favorites:false,sort:'recent'}");
  respond('/api/favoritos/10',null,204);await run('toggleFavorite(10)');assert.match(run('productCard(state.products[0])'),/favorite-toggle active/);
  respond('/api/favoritos/10',null,204);await run('toggleFavorite(10)');assert.doesNotMatch(run('productCard(state.products[0])'),/favorite-toggle active/);
  const css=fs.readFileSync(require('node:path').join(__dirname,'../src/main/resources/static/style.css'),'utf8');assert.ok(css.lastIndexOf('.favorite-toggle.active{color:#dc2626')>css.lastIndexOf('.studio-sell'));
 });
 await test('logout clears favorites and another account loads only its own list',async()=>{
  resetCalls();run("state.favorites=['10'];state.favoritesReady=true;state.filters.favorites=true");respond('/api/usuarios/logout',null,204);await run('logout()');assert.equal(run('state.favorites.length'),0);assert.equal(node('#favoriteCount').textContent,'');assert.equal(run('state.filters.favorites'),false);
  run("state.user={id:8,nome:'Outro'}");respond('/api/favoritos',[11]);await run('loadFavorites()');assert.equal(run('state.favorites.join()'),'11');
  run("clearFavorites();state.user={id:9,nome:'Player'}");respond('/api/favoritos',[10]);await run('loadFavorites()');assert.equal(run('state.favorites.join()'),'10');
 });
 await test('late favorite responses cannot restore a logged-out account list',async()=>{
  resetCalls();let release;queue.push({path:'/api/favoritos',body:[10],wait:new Promise(resolve=>release=resolve)});const pending=run('loadFavorites()');
  respond('/api/usuarios/logout',null,204);await run('logout()');release();await pending;assert.equal(run('state.favorites.length'),0);assert.equal(run('state.favoritesReady'),false);
 });
 await test('late favorite saves cannot add a heart after logout',async()=>{
  resetCalls();run("state.user={id:9,nome:'Player'};clearFavorites();state.favoritesReady=true");let release;queue.push({path:'/api/favoritos/10',status:204,wait:new Promise(resolve=>release=resolve)});const pending=run('toggleFavorite(10)');
  respond('/api/usuarios/logout',null,204);await run('logout()');release();await pending;assert.equal(run('state.favorites.length'),0);assert.equal(run('state.favoritesBusy'),false);
 });
 await test('only owner sees delete action and a denied request keeps the listing',async()=>{
  resetCalls();context.window.confirm=()=>true;run("state.products=[{id:10,nome:'Anúncio',preco:100,vendedorId:9}];state.user={id:8,nome:'Outro'};state.cart=[];state.favorites=[];state.deleteBusy=false;state.cartBusy=false;state.checkoutBusy=false;state.view='produto'");
  run("renderProduct('10')");assert.doesNotMatch(node('#app').innerHTML,/data-action="delete-listing"/);
  run("state.user={id:9,nome:'Vendedor'};renderProduct('10')");assert.match(node('#app').innerHTML,/data-action="delete-listing"/);
  respond('/api/anuncios/excluir/10',{message:'Sem permissão'},403);await run('deleteListing(10)');assert.equal(run('state.products.length'),1);
  respond('/api/anuncios/excluir/10',null,204);await run('deleteListing(10)');assert.equal(run('state.products.length'),0);
 });
 await test('Pix and boleto screens expose fictitious codes and their images',async()=>{
  run("state.user={id:9,nome:'Player'};state.view='pagamento';state.payment={pedidoId:42,formaPagamento:'pix',codigo:'SIMULACAO-PIX-TESTE',qrSvg:'<svg/>',valorTotal:125,status:'AGUARDANDO_PAGAMENTO'};renderPayment('42')");assert.match(node('#app').innerHTML,/SIMULACAO-PIX-TESTE/);assert.match(node('#app').innerHTML,/data:image\/svg/);
  run("state.payment.formaPagamento='boleto';state.payment.barrasSvg='<svg/>';state.payment.vencimento='2030-12-31';renderPayment('42')");assert.match(node('#app').innerHTML,/Seu boleto está pronto/);assert.match(node('#app').innerHTML,/31\/12\/2030/);
 });
 await test('card details are local; confirmation sends only the simulation code',async()=>{
  resetCalls();run("state.payment={pedidoId:42,formaPagamento:'cartao',codigo:'SIMULACAO-CARTAO-TESTE',valorTotal:125,status:'AGUARDANDO_PAGAMENTO'};state.orders=[];renderPayment('42')");
  node('#testCardName').value='PLAYER TESTE';node('#testCardNumber').value='4242 4242 4242 4242';node('#testCardExpiry').value='12/'+String((new Date().getFullYear()+2)%100);node('#testCardCvv').value='123';
  respond('/api/pedidos/42/simulacao/confirmar',{id:42,status:'PAGAMENTO_SIMULADO',valorTotal:125,formaPagamento:'cartao'});context.paymentEvent=event('#simulationPaymentForm');await run('confirmSimulation(paymentEvent)');
  assert.equal(JSON.stringify(JSON.parse(calls[0].options.body)),JSON.stringify({codigo:'SIMULACAO-CARTAO-TESTE'}));assert.equal(run('state.orders[0].status'),'PAGAMENTO_SIMULADO');assert.match(node('#app').innerHTML,/Compra confirmada/);
  const count=calls.length;await run('confirmSimulation(paymentEvent)');assert.equal(calls.length,count);
 });
 await test('a non-test card cannot confirm a simulated payment',async()=>{
  resetCalls();run("state.payment.status='AGUARDANDO_PAGAMENTO'");node('#testCardNumber').value='4111 1111 1111 1111';await run('confirmSimulation(paymentEvent)');assert.equal(calls.length,0);assert.match(node('#simulationPaymentError').textContent,/dados de exemplo/);
 });
 await test('local photo preview, multipart upload and image URL are used when publishing',async()=>{
  resetCalls();context.localPhoto=new Blob(['png fixture'],{type:'image/png'});Object.defineProperty(context.localPhoto,'name',{value:'minha-foto.png'});
  run("state.user={id:9,nome:'Player'};state.demo=false;state.categoriesReady=true;state.products=[];chooseSellerImage(localPhoto)");assert.equal(run('state.sellerImageFile.name'),'minha-foto.png');assert.equal(node('#announcementImagePreview').hidden,false);
  node('#announcementForm').fields={produto:'Produto com foto',preco:'100',categoriaId:'1',descricao:'Produto em bom estado com foto do computador.',imagemUrl:''};
  const url='/uploads/anuncios/11111111-2222-3333-4444-555555555555.png';respond('/api/imagens',{imagemUrl:url});respond('/api/anuncios/cadastrar',{id:120,produto:'Produto com foto',preco:100,categoriaId:1,vendedorId:9,imagemUrl:url});
  await run('handleCreateAnnouncement(announcementEvent)');assert.equal(node('#announcementError').textContent,'');assert.equal(calls[0].options.headers['Content-Type'],undefined);assert.equal(calls[0].options.body.get('arquivo').name,'minha-foto.png');assert.equal(JSON.parse(calls[1].options.body).imagemUrl,url);assert.equal(run('state.products[0].imagemUrl'),url);assert.equal(run('state.sellerImageFile'),null);
 });
 await test('upload error preserves the file and never creates a listing',async()=>{
  resetCalls();run('chooseSellerImage(localPhoto)');respond('/api/imagens',{message:'Não foi possível salvar a imagem'},500);await run('handleCreateAnnouncement(announcementEvent)');assert.equal(calls.length,1);assert.match(node('#announcementError').textContent,/salvar a imagem/);assert.equal(run('state.sellerImageFile.name'),'minha-foto.png');assert.equal(run('state.announcementBusy'),false);
 });
 await test('a retry reuses an uploaded image after a listing API failure',async()=>{
  resetCalls();const url='/uploads/anuncios/11111111-2222-3333-4444-555555555555.png';respond('/api/imagens',{imagemUrl:url});respond('/api/anuncios/cadastrar',{message:'Confira a categoria'},409);await run('handleCreateAnnouncement(announcementEvent)');assert.equal(calls.length,2);assert.equal(run('state.sellerUploadedImage'),url);
  resetCalls();respond('/api/anuncios/cadastrar',{id:121,produto:'Produto com foto',preco:100,categoriaId:1,vendedorId:9,imagemUrl:url});await run('handleCreateAnnouncement(announcementEvent)');assert.equal(calls.length,1);assert.equal(calls[0].url,'/api/anuncios/cadastrar');
 });
 await test('invalid and oversized local files cannot be selected',async()=>{
  context.invalidPhoto={name:'arquivo.svg',type:'image/svg+xml',size:20};run('chooseSellerImage(invalidPhoto)');assert.equal(run('state.sellerImageFile'),null);assert.match(node('#announcementError').textContent,/JPG ou PNG/);
  context.largePhoto={name:'foto.png',type:'image/png',size:6*1024*1024};run('chooseSellerImage(largePhoto)');assert.equal(run('state.sellerImageFile'),null);assert.match(node('#announcementError').textContent,/5 MB/);
 });
 await test('checkout has normal labels and example card details stay local',async()=>{
  run("state.user={id:9,nome:'Player'};state.payment={pedidoId:42,formaPagamento:'cartao',codigo:'GAMESRUN-CARTAO-TESTE',valorTotal:125,status:'AGUARDANDO_PAGAMENTO'};renderPayment('42');fillPaymentExample()");
  assert.match(node('#app').innerHTML,/Dados do cartão/);assert.match(node('#app').innerHTML,/Confirmar compra/);assert.doesNotMatch(node('#app').innerHTML,/AMBIENTE DE SIMULAÇÃO|compra simulada|cartão fictício/);assert.match(node('#app').innerHTML,/nenhum valor será cobrado/i);assert.equal(node('#testCardNumber').value,'4242 4242 4242 4242');assert.equal(run('validateTestCard()'),'');
 });
 console.log('All 30 integration checks passed.');
})().catch(err=>{console.error(err);process.exit(1);});

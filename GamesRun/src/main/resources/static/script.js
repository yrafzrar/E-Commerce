'use strict';
/* GamesRun — interface e integração com os endpoints do projeto enviado. */
const $ = (selector, root = document) => root.querySelector(selector);
const $$ = (selector, root = document) => [...root.querySelectorAll(selector)];
const escapeHTML = value => String(value ?? '').replace(/[&<>"']/g, char => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[char]));
const money = value => Number(value || 0).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
const fold = value => String(value ?? '').normalize('NFD').replace(/[\u0300-\u036f]/g, '').toLowerCase();
const idPath = value => encodeURIComponent(String(value));
const config = window.GAMESRUN_CONFIG || {};
const API_BASE_URL = String(config.apiBaseUrl || '').replace(/\/$/, '');
const icons = {
  search:'<circle cx="10.5" cy="10.5" r="6.5"/><path d="m16 16 4.5 4.5"/>',
  arrow:'<path d="M4 12h16m-6-6 6 6-6 6"/>',
  heart:'<path d="M20.8 4.6a5.5 5.5 0 0 0-7.8 0L12 5.7l-1.1-1.1a5.5 5.5 0 0 0-7.8 7.8L12 21l8.8-8.6a5.5 5.5 0 0 0 0-7.8Z"/>',
  bag:'<path d="M5 7h14l1 14H4L5 7Z"/><path d="M9 8V6a3 3 0 0 1 6 0v2"/>',
  user:'<circle cx="12" cy="8" r="3.5"/><path d="M5 21v-2a7 7 0 0 1 14 0v2"/>',
  plus:'<path d="M12 5v14M5 12h14"/>', close:'<path d="m6 6 12 12M6 18 18 6"/>',
  logout:'<path d="M10 5H4v14h6m4-12 5 5-5 5m-5-5h10"/>',
  eye:'<path d="M2 12s3-7 10-7 10 7 10 7-3 7-10 7S2 12 2 12Z"/><circle cx="12" cy="12" r="3"/>',
  shield:'<path d="m12 2 8 3v6c0 5-8 11-8 11S4 16 4 11V5l8-3Z"/><path d="m8 11 3 3 5-6"/>',
  gamepad:'<path d="M7 7h10c3 0 4 2 5 9 .4 3-2 4-4 2l-2-2H8l-2 2c-2 2-4.4 1-4-2 1-7 2-9 5-9Z"/><path d="M7 10v5m-2.5-2.5h5"/><circle cx="16" cy="11" r=".5"/><circle cx="18" cy="14" r=".5"/>',
  truck:'<path d="M2 5h12v12H2Zm12 5h4l4 4v3h-8"/><circle cx="6" cy="18" r="2"/><circle cx="18" cy="18" r="2"/>',
  box:'<path d="m12 2 9 5v10l-9 5-9-5V7l9-5Zm0 10L3 7m9 5 9-5m-9 5v10M7.5 4.5l9 5"/>',
  handheld:'<rect x="6" y="2" width="12" height="20" rx="2"/><path d="M8.5 5h7v7h-7Zm1 10v4m-2-2h4m3-1h1m1 2h1"/>',
  playstation:'<path d="M8 21V3c7-2 12 1 8 8l-4-1V7m-4 9c-12 4-5 7 2 5m3-6c13-3 13 3 1 5"/>',
  xbox:'<circle cx="12" cy="12" r="10"/><path d="M5 4c3 0 8 5 14 15M19 4c-3 0-8 5-14 15"/>',
  gem:'<path d="m2 9 5-6h10l5 6-10 13L2 9Zm0 0h20M7 3l5 19 5-19"/>',
  check:'<path d="m5 12 4 4L20 5"/>', info:'<circle cx="12" cy="12" r="10"/><path d="M12 11v6m0-10v1"/>',
  chevron:'<path d="m9 5 7 7-7 7"/>', filter:'<path d="M3 6h18M6 12h12m-9 6h6"/>',
};
function icon(name) { return `<svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">${icons[name] || icons.gamepad}</svg>`; }
function hydrateIcons(root = document) { $$('[data-icon]', root).forEach(node => { node.innerHTML = icon(node.dataset.icon); node.removeAttribute('data-icon'); }); }
function readStorage(key, fallback, storage = localStorage) { try { return JSON.parse(storage.getItem(key)) ?? fallback; } catch { return fallback; } }
function writeStorage(key, value, storage = localStorage) { try { storage.setItem(key, JSON.stringify(value)); } catch { /* A página continua em memória se o navegador bloquear o armazenamento. */ } }
function safeImage(value, fallback = 'assets/controller.svg') {
  if (/^assets\/[a-z0-9-]+\.svg$/i.test(value || '')) return (window.GAMESRUN_ASSETS || {})[value] || value;
  if (/^\/uploads\/anuncios\/[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}\.(jpg|png)$/.test(value || '')) return `${API_BASE_URL}${value}`;
  try { const url = new URL(value); return ['http:', 'https:'].includes(url.protocol) ? url.href : fallback; } catch { return fallback; }
}
const mockCategories = [
  {id:1,nome:'Nintendo 3DS',icon:'handheld',tone:'green'}, {id:2,nome:'Nintendo 64',icon:'gamepad',tone:'purple'},
  {id:3,nome:'PlayStation 5',icon:'playstation',tone:'blue'}, {id:4,nome:'Xbox Series',icon:'xbox',tone:'green'}, {id:5,nome:'Retro & Colecionáveis',icon:'gem',tone:'purple'}
];
const demoProducts = [
  {id:'demo-1',nome:'Nintendo 3DS XL — azul',preco:1190,categoriaId:1,asset:'3ds',tone:'blue',estadoConservacao:'Usado - Como Novo',seller:'Retro Station'},
  {id:'demo-2',nome:'PlayStation 5 Slim · 1 TB',preco:3299.90,categoriaId:3,asset:'ps5',tone:'lavender',estadoConservacao:'Novo - Lacrado',seller:'Next Level'},
  {id:'demo-3',nome:'Game Boy Classic original',preco:649.90,categoriaId:5,asset:'handheld',tone:'green',estadoConservacao:'Usado - Bom Estado',seller:'Player 01'},
  {id:'demo-4',nome:'Nintendo 64 + controle',preco:899,categoriaId:2,asset:'n64',tone:'sand',estadoConservacao:'Usado - Bom Estado',seller:'Volta ao Jogo'},
  {id:'demo-5',nome:'Xbox Series X · 1 TB',preco:3490,categoriaId:4,asset:'xbox',tone:'green',estadoConservacao:'Usado - Como Novo',seller:'Next Level'},
  {id:'demo-6',nome:'DualSense — branco',preco:389.90,categoriaId:3,asset:'controller',tone:'blue',estadoConservacao:'Novo - Lacrado',seller:'Player 02'},
  {id:'demo-7',nome:'Cartucho retrô — edição especial',preco:249.90,categoriaId:5,asset:'cartridge',tone:'sand',estadoConservacao:'Usado - Bom Estado',seller:'Retro Station'},
  {id:'demo-8',nome:'Console portátil + controles',preco:1799,categoriaId:5,asset:'switch',tone:'lavender',estadoConservacao:'Usado - Como Novo',seller:'Volta ao Jogo'}
].map(product => ({...product, categoria:mockCategories.find(cat=>cat.id===product.categoriaId),imagemUrl:`assets/${product.asset}.svg`,vendedor:{nome:product.seller},demo:true,descricao:'Item ilustrativo do catálogo de demonstração. Os preços e vendedores são exemplos para visualizar a interface. Conecte o servidor para carregar os anúncios reais.'}));
function cleanCart(items) { return Array.isArray(items) ? items.filter(item => item && item.id != null && Number(item.quantidade)>0 && Number.isFinite(Number(item.preco)) && Number(item.preco)>=0).map(item=>({...item,quantidade:Math.min(99,Math.floor(Number(item.quantidade))),preco:Number(item.preco)})) : []; }
function cleanUser(user) { return user && user.id != null ? {id:user.id,nome:String(user.nome || 'Player'),nick:String(user.nick || '')} : null; }
const storedUser = cleanUser(readStorage('gamesrun_live_v1_session_v2', null, sessionStorage));
const state = {
  user:storedUser, products:[], categories:[], categoriesReady:false, demo:true, loading:true,
  cart:cleanCart(readStorage(storedUser ? `gamesrun_live_v1_cart_v2_user_${storedUser.id}` : 'gamesrun_live_v1_cart_v2_guest', [])),
  favorites:[], favoritesReady:false, favoritesBusy:false, favoritesVersion:0, orders:[], ordersError:'', view:'home',
  filters:{query:'',category:'',platform:'',condition:'',price:'',favorites:false,sort:'recent'}, limit:8,
  sellerImageFile:null,sellerImagePreview:'',sellerUploadedImage:'',announcementBusy:false,cartBusy:false, checkoutBusy:false, accountBusy:false, chat:[], afterAuth:null, payment:null,loadingPaymentId:null,confirmPaymentBusy:false,deleteBusy:false,connectionError:'', cartSyncError:'', sellerDraft:{}, checkoutDraft:{endereco:'',payment:'pix'}
};
// A antiga lista compartilhada não tinha dono identificável e não deve ser importada.
try { localStorage.removeItem('gamesrun_live_v1_favorites_v2'); } catch {}
function cartKey() { return state.user ? `gamesrun_live_v1_cart_v2_user_${state.user.id}` : 'gamesrun_live_v1_cart_v2_guest'; }
function saveCart() { writeStorage(cartKey(), state.cart); updateHeader(); }
function updateHeader() {
  $('#cartCountBadge').textContent = state.cart.reduce((sum,item)=>sum+item.quantidade,0);
  $('#favoriteCount').textContent = state.favorites.length || '';
  $('#userNameDisplay').innerHTML = state.user ? `${escapeHTML(state.user.nome.split(' ')[0])}<small>Minha conta</small>` : 'Entrar <small>ou criar conta</small>';
  $('#logoutButton').hidden = !state.user;
  $$('[data-nav]').forEach(link=>{if(link.dataset.nav===state.view)link.setAttribute('aria-current','page');else link.removeAttribute('aria-current');});
}
async function api(endpoint, options = {}) {
  if (config.demoOnly) throw new Error('Esta prévia usa dados de demonstração. Use o projeto completo com o servidor para acessar sua conta.');
  if (location.protocol === 'file:' && !API_BASE_URL) throw new Error('Abra o projeto pelo servidor para conectar sua conta.');
  const controller = new AbortController();
  const timer = setTimeout(()=>controller.abort(), Number(config.timeoutMs) || 6000);
  try {
    const response = await fetch(`${API_BASE_URL}${endpoint}`, {...options,credentials:'same-origin',headers:{Accept:'application/json',...(options.body&&!(options.body instanceof FormData)?{'Content-Type':'application/json'}:{}),...options.headers},signal:controller.signal});
    if (response.status === 204) return true;
    const text = await response.text(); let data;
    try { data=text ? JSON.parse(text) : null; } catch { throw new Error('O servidor não retornou JSON. Confira o endereço da API.'); }
    if (!response.ok) { const error=new Error(data?.message || data?.mensagem || data?.error || `Não foi possível concluir (${response.status}).`);error.status=response.status;throw error; }
    return data;
  } catch(error) {
    if ((error.name === 'AbortError'||error instanceof TypeError)&&endpoint==='/api/pedidos/cadastrar') throw new Error('Não foi possível confirmar o registro. Consulte seus pedidos antes de tentar novamente.');
    if (error.name === 'AbortError') throw new Error('O servidor demorou para responder. Tente novamente.');
    if (error instanceof TypeError) throw new Error('Sem conexão com o servidor. Tente novamente quando ele estiver disponível.');
    throw error;
  } finally { clearTimeout(timer); }
}
function normalizeAnnouncement(item) {
  const category=state.categories.find(cat=>String(cat.id)===String(item.categoriaId ?? item.categoria?.id));
  return {...item,id:item.id,nome:item.produto || item.nome || 'Produto sem título',preco:Number(item.preco)||0,categoria:category || item.categoria || {nome:'Gamer'},imagemUrl:safeImage(item.imagemUrl),estadoConservacao:item.estadoConservacao || 'Não informado',vendedor:item.vendedor || {nome:item.vendedorId?`Vendedor #${item.vendedorId}`:'Vendedor'},demo:false};
}
async function initApp() {
  clearFavorites();
  state.loading=true;state.connectionError='';
  hydrateIcons();$('#year').textContent=new Date().getFullYear();updateHeader();
  $('#app').innerHTML='<div class="initial-loader"><span class="loading-dot"></span> Carregando seus próximos achados…</div>';
  if(config.demoOnly){state.products=[...demoProducts];state.categories=mockCategories;state.demo=true;state.loading=false;renderRoute();return;}
  try {
    const [categories,products]=await Promise.all([api('/api/categorias/listar'),api('/api/anuncios/listar')]);
    if(!Array.isArray(categories)||!Array.isArray(products))throw new Error('O catálogo retornado pelo servidor é inválido.');
    state.categories=categories;state.categoriesReady=true;state.products=products.map(normalizeAnnouncement);state.demo=false;
    try {
      state.user=cleanUser(await api('/api/usuarios/sessao'));
      writeStorage('gamesrun_live_v1_session_v2',state.user,sessionStorage);
    } catch(error) {
      if(error.status!==401)throw error;
      state.user=null;try{sessionStorage.removeItem('gamesrun_live_v1_session_v2');}catch{}
    }
    state.cart=cleanCart(readStorage(cartKey(),[]));
    if(state.user){await loadFavorites();await refreshAccount(true);}
  }catch(error){state.connectionError=error.message;}
  state.loading=false;renderRoute();
}
function navigate(view, id) { const hash=`#${view}${id!=null?'/'+encodeURIComponent(id):''}`; if(location.hash===hash)renderRoute();else location.hash=hash; }
function renderRoute() {
  if (state.loading) return;
  if(state.connectionError){$('#app').innerHTML=pageHeader('Não conseguimos carregar o catálogo.')+empty('Vamos tentar novamente?',state.connectionError,'Recarregar catálogo','retry-app','info');return;}
  const [route='home',encodedId] = location.hash.slice(1).split('/');
  let id=''; try { id=decodeURIComponent(encodedId || ''); } catch { /* Link inválido recebe estado vazio. */ }
  state.view=route || 'home';
  const renderers={home:renderHome,produto:()=>renderProduct(id),carrinho:renderCart,checkout:renderCheckout,vendedor:renderSeller,pedidos:renderOrders,pagamento:()=>renderPayment(id),chat:renderChat};
  (renderers[state.view] || renderHome)(); updateHeader();
  if(state.view==='vendedor'){for(const [name,value] of Object.entries(state.sellerDraft)){const input=$(`[name="${name}"]`, $('#announcementForm'));if(input&&input.type!=='file')input.value=value;}updateSellerImagePreview();}
  if(state.view==='checkout'&&$('#checkoutAddress')){$('#checkoutAddress').value=state.checkoutDraft.endereco;const radio=$(`[name="payment"][value="${state.checkoutDraft.payment}"]`);if(radio)radio.checked=true;updatePaymentExplanation(state.checkoutDraft.payment);}
  const titles={home:'Dê play na sua próxima história',produto:state.products.find(p=>String(p.id)===id)?.nome || 'Produto',carrinho:'Seu carrinho',checkout:'Finalizar pedido',vendedor:'Criar anúncio',pedidos:'Minha conta',pagamento:'Pagamento',chat:'Assistente de segurança'};
  document.title=`GamesRun Studio — ${titles[state.view] || titles.home}`;
}
function pageHeader(title,subtitle='') { return `<div class="page-header"><div class="breadcrumb"><a href="#home">Início</a><span>/</span><span>${escapeHTML(title)}</span></div><h1>${escapeHTML(title)}</h1>${subtitle?`<p>${escapeHTML(subtitle)}</p>`:''}</div>`; }
function empty(title,description,button='Explorar produtos',action='reset',symbol='gamepad') { return `<div class="empty-state">${icon(symbol)}<h2>${escapeHTML(title)}</h2><p>${escapeHTML(description)}</p><button class="btn btn-primary" data-action="${action}">${escapeHTML(button)} ${icon('arrow')}</button></div>`; }
function renderHome() {
 $('#app').innerHTML = `<section class="studio-hero" aria-labelledby="heroTitle"><div class="studio-hero-copy"><span class="studio-kicker">NOVAS HISTÓRIAS. O MESMO PLAY.</span><h1 id="heroTitle">O próximo<br>jogo <span>é seu.</span></h1><p>Consoles, jogos e clássicos que merecem uma nova história. Encontre o seu próximo favorito.</p><div class="studio-actions"><button class="btn btn-primary" data-action="explore">Encontrar meu próximo play ${icon('arrow')}</button><a href="#vendedor" class="studio-text-link">Vender um produto</a></div><div class="studio-hero-foot"><span>01 — DESCOBERTA</span><span>DE PLAYER PARA PLAYER</span></div></div><div class="studio-hero-art"><div class="studio-orbit"></div><span class="studio-art-word" aria-hidden="true">PLAY</span><img class="studio-ps5" src="assets/ps5.svg" alt="Ilustração de um PlayStation 5 e controle"><span class="studio-edition">O seu setup.<br><b>Um novo capítulo.</b></span><span class="studio-small-cross" aria-hidden="true">✳</span><div class="studio-product-label"><span class="studio-label-dot"></span><span>Do clássico ao next-gen.</span>${icon('arrow')}</div></div></section>
<div class="studio-marquee"><span>ENCONTRE SEU UNIVERSO</span><span>NINTENDO</span><span>PLAYSTATION</span><span>XBOX</span><span>RETRÔ & COLECIONÁVEIS</span><span>✳</span></div>
<section class="studio-categories"><div class="section-heading"><h2>Por onde vamos começar?</h2><button class="section-link" data-action="reset">Explorar tudo ${icon('arrow')}</button></div><div class="category-grid">${state.categories.map((cat,index)=>`<button class="category-card ${state.filters.category===String(cat.id)?'active':''}" data-action="category" data-id="${escapeHTML(cat.id)}" aria-pressed="${state.filters.category===String(cat.id)}"><span class="category-icon">${icon(cat.icon || (/xbox/i.test(cat.nome)?'xbox':/playstation/i.test(cat.nome)?'playstation':/3ds/i.test(cat.nome)?'handheld':'gamepad'))}</span><span><strong>${escapeHTML(cat.nome)}</strong><small>Explorar coleção</small></span>${icon('chevron')}</button>`).join('')}</div></section>
<section class="section catalog-section" id="catalog" aria-labelledby="catalogTitle"><div class="section-heading"><div><h2 id="catalogTitle">Seu próximo achado.</h2><p>Um novo level para o seu setup e a sua coleção.</p></div><div class="catalog-meta"><span id="productCount" class="muted"></span><span class="${state.demo?'demo-label':'live-label'}">${state.demo?'Catálogo demonstrativo':'Anúncios do marketplace'}</span></div></div>${state.demo?'<p class="notice warning">Você está explorando exemplos ilustrativos. Preços e vendedores são fictícios; os anúncios reais aparecem quando o servidor estiver disponível.</p>':''}
  <div class="catalog-toolbar"><div class="filters"><button class="filter-pill ${!state.filters.condition&&!state.filters.favorites?'active':''}" data-action="all">Todos</button><button class="filter-pill ${state.filters.condition==='novo'?'active':''}" data-action="condition" data-value="novo">Novos & lacrados</button><button class="filter-pill ${state.filters.condition==='usado'?'active':''}" data-action="condition" data-value="usado">Usados</button><button class="filter-pill ${state.filters.favorites?'active':''}" data-action="favorites">Favoritos</button><label class="sr-only" for="priceFilter">Faixa de preço</label><select class="filter-select" id="priceFilter"><option value="">Qualquer preço</option><option value="500">Até R$ 500</option><option value="1500">Até R$ 1.500</option><option value="3000">Até R$ 3.000</option></select><button class="section-link" data-action="reset" title="Limpar todos os filtros">Limpar</button></div><label class="sort-label" for="sortProducts"><span>Ordenar por</span><select class="filter-select" id="sortProducts"><option value="recent">Mais recentes</option><option value="price-asc">Menor preço</option><option value="price-desc">Maior preço</option><option value="name">Nome: A–Z</option></select></label></div><div id="activeFilterDescription" class="field-help" role="status"></div><div id="productGrid" class="product-grid"></div><div class="catalog-bottom"><button id="loadMore" class="btn btn-secondary" data-action="more">Carregar mais ${icon('plus')}</button></div></section>
<section class="studio-sell"><div><span class="eyebrow">UMA NOVA FASE PARA O QUE VOCÊ JÁ JOGOU</span><h2>Passa o controle.<br>Continua a história.</h2><p>Aquele console na estante pode ser a próxima descoberta de alguém.</p><a href="#vendedor" class="btn btn-primary">Criar meu anúncio ${icon('arrow')}</a></div><img src="assets/controller.svg" alt="Ilustração de um controle de videogame"><span class="studio-sell-symbol" aria-hidden="true">↗</span></section>
`;
 $('#searchInput').value=state.filters.query;
 $('#priceFilter').value=state.filters.price;
 $('#sortProducts').value=state.filters.sort;
 renderCatalog();
}
function filteredProducts() {
  const f=state.filters;
  let products=state.products.filter(p=>(!f.query||fold(`${p.nome} ${p.categoria?.nome} ${p.descricao || ''}`).includes(fold(f.query))) && (!f.category||String(p.categoria?.id)===f.category) && (!f.platform||fold(p.categoria?.nome).includes(fold(f.platform))) && (!f.condition||(f.condition==='novo'?!fold(p.estadoConservacao).includes('usado')&&fold(p.estadoConservacao).includes('novo'):fold(p.estadoConservacao).includes('usado'))) && (!f.price||p.preco<=Number(f.price)) && (!f.favorites||state.favorites.includes(String(p.id))));
  if(f.sort==='price-asc') products.sort((a,b)=>a.preco-b.preco);
  if(f.sort==='price-desc') products.sort((a,b)=>b.preco-a.preco);
  if(f.sort==='name') products.sort((a,b)=>a.nome.localeCompare(b.nome,'pt-BR'));
  if(f.sort==='recent' && !state.demo) products.sort((a,b)=>{const dateA=Date.parse(a.dataCriacao),dateB=Date.parse(b.dataCriacao);return Number.isFinite(dateA)&&Number.isFinite(dateB)?dateB-dateA:(Number(b.id)||0)-(Number(a.id)||0);});
  return products;
}
function productCard(product) {
  const p=product, favorite=state.favorites.includes(String(p.id)), name=String(p.vendedor?.nome || 'Vendedor');
  const condition=fold(p.estadoConservacao).includes('lacrado')?'NOVO · LACRADO':fold(p.estadoConservacao).includes('como novo')?'COMO NOVO':fold(p.estadoConservacao).includes('bom')?'BOM ESTADO':p.estadoConservacao;
  return `<article class="product-card"><div class="product-picture" data-tone="${escapeHTML(p.tone || 'sand')}"><a href="#produto/${idPath(p.id)}" class="product-image-link" tabindex="-1" aria-hidden="true"><img src="${escapeHTML(safeImage(p.imagemUrl))}" alt="" loading="lazy"></a><span class="product-badge">${escapeHTML(condition)}</span><button class="favorite-toggle ${favorite?'active':''}" data-action="favorite" data-id="${escapeHTML(p.id)}" aria-label="${favorite?'Remover dos':'Adicionar aos'} favoritos: ${escapeHTML(p.nome)}" aria-pressed="${favorite}">${icon('heart')}</button></div><div class="product-info"><span class="product-type">${escapeHTML(p.categoria?.nome || 'Gamer')}</span><h3><a href="#produto/${idPath(p.id)}">${escapeHTML(p.nome)}</a></h3><p class="product-price">${money(p.preco)}</p><p class="product-caption">${p.demo?'Produto ilustrativo':'Preço anunciado pelo vendedor'}</p><div class="product-seller"><span class="seller-name"><span class="seller-avatar" aria-hidden="true">${escapeHTML(name.split(' ').map(x=>x[0]).join('').slice(0,2))}</span><span>${escapeHTML(name)}</span></span><button class="add-button" data-action="add-cart" data-id="${escapeHTML(p.id)}" aria-label="Adicionar ao carrinho: ${escapeHTML(p.nome)}">${icon('plus')}</button></div></div></article>`;
}
function renderCatalog() {
  if (!$('#productGrid')) return;
  const products=filteredProducts();
  $('#productCount').textContent=`${products.length} ${products.length===1?'item encontrado':'itens encontrados'}`;
  $('#productGrid').innerHTML=state.products.length===0?empty('O primeiro achado pode ser seu.','Ainda não há anúncios publicados. Crie sua conta e anuncie um produto.','Criar anúncio','seller','box'):products.length?products.slice(0,state.limit).map(productCard).join(''):empty('Nenhum achado por aqui.','Tente outro termo ou ajuste os filtros.','Limpar filtros','reset','search');
  $('#loadMore').hidden=products.length<=state.limit;
  const f=state.filters;
  $('#activeFilterDescription').textContent=[f.query?`Busca: “${f.query}”`:'',f.platform?`Universo: ${f.platform}`:'',f.category?`Categoria: ${state.categories.find(c=>String(c.id)===f.category)?.nome || ''}`:'',f.favorites?'Seus favoritos':''].filter(Boolean).join(' · ');
}
function showCatalog(scroll=true) { state.limit=8;if(state.view!=='home')navigate('home');else renderHome(); if(scroll)requestAnimationFrame(()=>$('#catalog')?.scrollIntoView({behavior:'smooth'})); }
function resetFilters() { state.filters={query:'',category:'',platform:'',condition:'',price:'',favorites:false,sort:'recent'};showCatalog(); }
function renderProduct(id) {
  const p=state.products.find(item=>String(item.id)===id);
  if(!p){$('#app').innerHTML=pageHeader('Produto não encontrado')+empty('Esse item não está disponível.','Volte para explorar os anúncios.');return;}
  $('#app').innerHTML=pageHeader('Detalhes do produto')+`<div class="product-detail"><div class="detail-photo"><img src="${escapeHTML(safeImage(p.imagemUrl))}" alt="${escapeHTML(p.nome)}"><span class="product-badge">${escapeHTML(p.categoria?.nome)}</span></div><div class="detail-copy"><span class="eyebrow accent">${escapeHTML(p.categoria?.nome)}</span><h1>${escapeHTML(p.nome)}</h1><span class="condition-label">${escapeHTML(p.estadoConservacao)}</span><p class="detail-price">${money(p.preco)}</p><p class="field-help">${p.demo?'Preço ilustrativo para demonstração':'Preço anunciado pelo vendedor'}</p><dl class="detail-facts"><div><dt>Vendedor</dt><dd>${escapeHTML(p.vendedor?.nome)}</dd></div><div><dt>Conservação</dt><dd>${escapeHTML(p.estadoConservacao)}</dd></div><div><dt>Entrega padrão</dt><dd>${money(25)}</dd></div></dl><div class="detail-buttons"><button class="btn btn-primary" data-action="buy" data-id="${escapeHTML(p.id)}">Adicionar e continuar ${icon('arrow')}</button><button class="btn btn-secondary favorite-detail ${state.favorites.includes(String(p.id))?'active':''}" data-action="favorite" aria-pressed="${state.favorites.includes(String(p.id))}" data-id="${escapeHTML(p.id)}">${icon('heart')} ${state.favorites.includes(String(p.id))?'Salvo':'Salvar'}</button>${ownsProduct(p)?`<button type="button" class="btn btn-secondary delete-listing" data-action="delete-listing" data-id="${escapeHTML(p.id)}">Excluir anúncio</button>`:''}</div><p class="notice ${p.demo?'warning':''}" style="margin-top:20px">${p.demo?'Item de demonstração: explore o carrinho, sem realizar uma compra real.':'Confira a descrição e o estado do produto antes de registrar seu pedido.'}</p></div></div><section class="panel description"><h2>Sobre este achado</h2><p>${escapeHTML(p.descricao || 'O vendedor ainda não adicionou uma descrição detalhada.')}</p></section>`;
}
function ownsProduct(product){return Boolean(state.user&&String(product.vendedorId)===String(state.user.id));}
function ownListings(){
  if(!state.user)return '';
  const products=state.products.filter(ownsProduct);
  return `<section class="panel own-listings"><h2>Meus anúncios</h2>${products.length?products.map(p=>`<div class="own-listing-row"><div><a href="#produto/${idPath(p.id)}">${escapeHTML(p.nome)}</a><small>${money(p.preco)}</small></div><button type="button" class="btn btn-secondary delete-listing" data-action="delete-listing" data-id="${escapeHTML(p.id)}">Excluir anúncio</button></div>`).join(''):'<p class="muted">Você ainda não publicou anúncios.</p>'}</section>`;
}
async function deleteListing(id){
  const product=state.products.find(p=>String(p.id)===String(id));
  if(!product||!ownsProduct(product)||state.deleteBusy||state.checkoutBusy||state.cartBusy)return;
  if(!window.confirm(`Excluir “${product.nome}” do banco de dados? Esta ação não pode ser desfeita.`))return;
  state.deleteBusy=true;
  try{
    await api(`/api/anuncios/excluir/${idPath(id)}`,{method:'DELETE'});
    state.products=state.products.filter(p=>String(p.id)!==String(id));
    state.cart=state.cart.filter(p=>String(p.id)!==String(id));
    state.favorites=state.favorites.filter(f=>f!==String(id));
    saveCart();
    if(state.view==='produto')navigate('home');else renderRoute();
    showToast('Anúncio excluído do banco de dados.','success');
  }catch(error){showToast(error.message,'error');if(error.status===401)openAuth();}
  finally{state.deleteBusy=false;}
}
function subtotal() { return state.cart.reduce((sum,item)=>sum+item.preco*item.quantidade,0); }
function hasDemoItems() { return state.cart.some(item=>item.demo || String(item.id).startsWith('demo-')); }
function summary(checkout=false) { return `<aside class="panel order-summary"><h2>Resumo do pedido</h2><div class="summary-line"><span>Produtos (${state.cart.reduce((n,i)=>n+i.quantidade,0)})</span><strong>${money(subtotal())}</strong></div><div class="summary-line"><span>Entrega padrão</span><strong>${money(25)}</strong></div><div class="summary-line total"><span>Total</span><strong>${money(subtotal()+25)}</strong></div>${!checkout?'<a href="#checkout" class="btn btn-primary full">Continuar pedido '+icon('arrow')+'</a>':''}<p class="summary-note">Frete fixo de R$ 25,00 por pedido.<br>Escolha Pix, cartão ou boleto na próxima etapa.</p>${hasDemoItems()?'<p class="notice warning" style="margin-top:16px">Este carrinho contém produtos de demonstração. Nenhuma compra real será registrada.</p>':''}</aside>`; }
function cartSyncNotice(){return state.cartSyncError?`<p class="notice warning" style="margin-bottom:20px">${escapeHTML(state.cartSyncError)} <button class="section-link" data-action="sync-cart">Tentar sincronizar →</button></p>`:'';}
function renderCart() {
  $('#app').innerHTML=cartSyncNotice()+pageHeader('Seu carrinho. Seu próximo level.','Confira seus achados antes de continuar.')+(state.cart.length?`<div class="two-columns"><section class="panel" aria-label="Itens do carrinho">${state.cart.map(item=>`<article class="cart-item"><img src="${escapeHTML(safeImage(item.imagemUrl))}" alt="${escapeHTML(item.nome)}"><div><h2>${escapeHTML(item.nome)}</h2><p>${money(item.preco)} por item</p><div class="quantity"><button data-action="quantity" data-id="${escapeHTML(item.id)}" data-delta="-1" aria-label="Diminuir quantidade de ${escapeHTML(item.nome)}" ${state.cartBusy?'disabled':''}>−</button><span>${item.quantidade}</span><button data-action="quantity" data-id="${escapeHTML(item.id)}" data-delta="1" aria-label="Aumentar quantidade de ${escapeHTML(item.nome)}" ${state.cartBusy||item.quantidade>=99?'disabled':''}>+</button></div></div><div class="cart-item-total">${money(item.preco*item.quantidade)}<button class="remove-link" data-action="remove" data-id="${escapeHTML(item.id)}" ${state.cartBusy?'disabled':''}>Remover</button></div></article>`).join('')}</section>${summary()}</div>`:empty('O próximo play ainda não chegou.','Explore os produtos e adicione seus favoritos ao carrinho.','Explorar produtos','reset','bag'));
}
async function addToCart(id) {
  if(state.checkoutBusy){showToast('Aguarde o registro do pedido antes de alterar o carrinho.','info');return false;}
  if(state.cartBusy)return false;
  if(state.cartSyncError&&state.user){showToast('Sincronize o carrinho da sua conta antes de adicionar produtos.','error');return false;}
  const product=state.products.find(p=>String(p.id)===String(id)); if(!product)return false;
  const existing=state.cart.find(item=>String(item.id)===String(id));
  if(existing?.quantidade>=99){showToast('Limite de 99 unidades por item.','info');return false;}
  state.cartBusy=true;
  try {
    if(state.user&&!product.demo)await api(`/api/carrinhos/usuario/${idPath(state.user.id)}/itens`,{method:'POST',body:JSON.stringify({anuncioId:product.id,quantidade:1})});
    if(existing)existing.quantidade++;else state.cart.push({...product,quantidade:1});
    saveCart();if(state.view==='carrinho')renderCart();showToast(`${product.nome} está no seu carrinho.`,'success','Adicionado!');return true;
  }catch(error){showToast(error.message,'error');return false;}finally{state.cartBusy=false;if(state.view==='carrinho')renderCart();}
}
async function changeQuantity(id,delta,remove=false) {
  if(state.cartBusy||state.checkoutBusy)return;
  if(state.cartSyncError&&state.user){showToast('Sincronize seu carrinho antes de alterá-lo.','error');return;}
  const item=state.cart.find(i=>String(i.id)===String(id));if(!item)return;
  const quantity=remove?0:Math.min(99,item.quantidade+delta);state.cartBusy=true;renderCart();
  try {
    if(state.user&&!item.demo) { const path=`/api/carrinhos/usuario/${idPath(state.user.id)}/itens/${idPath(item.id)}`;await api(quantity>0?`${path}?quantidade=${quantity}`:path,{method:quantity>0?'PUT':'DELETE'}); }
    if(quantity<=0)state.cart=state.cart.filter(i=>String(i.id)!==String(id));else item.quantidade=quantity;saveCart();
  }catch(error){showToast(error.message,'error');}finally{state.cartBusy=false;if(state.view==='carrinho')renderCart();}
}
function updatePaymentExplanation(method) {
  const target=$('#paymentExplanation');if(!target)return;
  const messages={pix:'Na próxima etapa, você terá o QR Code e o código Pix para copiar.',cartao:'Preencha os dados do cartão na próxima etapa para concluir a compra.',boleto:'Na próxima etapa, você terá o boleto e a linha digitável para copiar.'};
  target.textContent=messages[method]||messages.pix;
}
function renderCheckout() {
  if(!state.cart.length){$('#app').innerHTML=pageHeader('Finalizar pedido')+empty('Seu carrinho está vazio.','Escolha um produto antes de continuar.');return;}
  const gated=!state.user;
  $('#app').innerHTML=cartSyncNotice()+pageHeader('Quase no próximo level.','Confira a entrega e escolha como deseja pagar.')+`<div class="checkout-steps"><span>01 Carrinho</span><span class="active">02 Entrega e pagamento</span><span>03 Pedido registrado</span></div><div class="two-columns"><section class="panel form-panel"><h2>Detalhes da entrega</h2>${gated?'<p class="notice" style="margin-bottom:20px">Entre ou crie sua conta para registrar o pedido. <button class="section-link accent" data-action="checkout-auth">Entrar agora →</button></p>':''}<form id="checkoutForm"><label for="checkoutAddress">Endereço completo</label><textarea id="checkoutAddress" name="endereco" required minlength="15" maxlength="255" autocomplete="street-address" placeholder="Rua, número, complemento, bairro, cidade, UF e CEP" rows="3"></textarea><hr class="form-separator"><h2>Forma de pagamento</h2><div class="payment-methods"><label class="payment-option"><input type="radio" name="payment" value="pix" checked> Pix</label><label class="payment-option"><input type="radio" name="payment" value="cartao"> Cartão</label><label class="payment-option"><input type="radio" name="payment" value="boleto"> Boleto</label></div><p id="paymentExplanation" class="field-help">Escolha como deseja pagar seu pedido.</p><p id="checkoutError" class="form-error" role="alert"></p><button type="submit" class="btn btn-primary full" ${hasDemoItems()?'disabled':''}>${gated?'Entrar para continuar':'Continuar para pagamento'} ${icon('arrow')}</button><p class="payment-environment">Ambiente de teste · nenhum valor será cobrado.</p></form></section>${summary(true)}</div>`;
}
async function executeCheckout(event) {
  event.preventDefault();if(state.checkoutBusy||!state.cart.length)return;
  if(!state.user){state.checkoutDraft={endereco:$('#checkoutAddress').value,payment:new FormData(event.target).get('payment')};openAuth('login',()=>navigate('checkout'));return;}
  if(hasDemoItems()){showToast('Produtos de demonstração não podem gerar pedidos.','error');return;}
  if(state.cartSyncError){showToast('Sincronize seu carrinho antes de registrar o pedido.','error');return;}
  const form=event.target, button=$('button[type="submit"]',form), error=$('#checkoutError');
  state.checkoutBusy=true;button.disabled=true;button.textContent='Registrando pedido…';error.textContent='';
  const items=state.cart.map(item=>({...item}));
  const payload={compradorId:state.user.id,status:'AGUARDANDO_PAGAMENTO',dataCriacao:new Date().toISOString(),valorTotal:subtotal()+25,formaPagamento:new FormData(form).get('payment'),enderecoEntrega:$('#checkoutAddress').value.trim(),itensResumo:JSON.stringify(items.map(({nome,preco,quantidade})=>({nome,preco,quantidade})))};
  if(payload.itensResumo.length>4000){error.textContent='Este carrinho tem muitos itens para um pedido. Divida a compra em pedidos menores.';state.checkoutBusy=false;button.disabled=false;button.innerHTML='Registrar pedido '+icon('arrow');return;}
  try {
    const result=await api('/api/pedidos/cadastrar',{method:'POST',body:JSON.stringify(payload)});
    if(!result||result.id==null)throw new Error('O servidor não confirmou o pedido. Confira seus pedidos antes de tentar novamente.');
    // Depois da confirmação do pedido, uma falha ao limpar o carrinho não deve repetir a compra.
    state.orders.unshift(normalizeOrder({...payload,...result}));state.ordersError='';state.cart=[];state.checkoutDraft={endereco:'',payment:'pix'};saveCart();
    const cleanup=await Promise.allSettled(items.map(item=>api(`/api/carrinhos/usuario/${idPath(state.user.id)}/itens/${idPath(item.id)}`,{method:'DELETE'})));
    state.payment=null;navigate('pagamento',result.id);showToast(`Pedido #${result.id} registrado. Continue para concluir o pagamento.`,'success','Próximo passo');
    if(cleanup.some(r=>r.status==='rejected'))showToast('Seu pedido foi registrado, mas alguns itens continuam no carrinho do servidor. Não registre o pedido novamente.','info');
  }catch(err){error.textContent=err.message;showToast(err.message,'error');}finally{state.checkoutBusy=false;button.disabled=false;button.innerHTML='Registrar pedido '+icon('arrow');}
}
function svgImage(svg){return 'data:image/svg+xml;charset=utf-8,'+encodeURIComponent(svg || '');}
function renderPayment(id){
  if(!state.user){$('#app').innerHTML=pageHeader('Pagamento')+empty('Entre para continuar.','Acesse sua conta para concluir o pedido.','Entrar na conta','account','user');return;}
  const p=state.payment;
  if(!p||String(p.pedidoId)!==String(id)){
    $('#app').innerHTML=pageHeader('Preparando seu pagamento.')+'<div class="initial-loader"><span class="loading-dot"></span> Carregando os dados de pagamento…</div>';
    if(state.loadingPaymentId!==String(id))loadPayment(id);
    return;
  }
  const method={pix:'Pix',cartao:'Cartão',boleto:'Boleto'}[p.formaPagamento] || 'Pagamento';
  if(p.status==='PAGAMENTO_SIMULADO'){
    $('#app').innerHTML=pageHeader('Compra confirmada.')+`<section class="panel payment-receipt"><div class="receipt-check">${icon('check')}</div><span class="eyebrow">PEDIDO CONFIRMADO</span><h2>Deu play na próxima história.</h2><p>Pedido #${escapeHTML(p.pedidoId)} · ${escapeHTML(method)}</p><strong class="receipt-total">${money(p.valorTotal)}</strong><a href="#pedidos" class="btn btn-primary">Ver meus pedidos ${icon('arrow')}</a><a href="#home" class="btn btn-secondary">Continuar explorando</a><p class="payment-environment">Ambiente de teste · nenhum valor foi cobrado.</p></section>`;return;
  }
  const code=`<label for="simulationCode">${p.formaPagamento==='pix'?'Código Pix copia e cola':'Linha digitável'}</label><textarea id="simulationCode" class="simulation-code" readonly rows="3">${escapeHTML(p.codigo)}</textarea><button type="button" class="btn btn-secondary" data-action="copy-payment">${p.formaPagamento==='pix'?'Copiar código Pix':'Copiar linha digitável'}</button>`;
  const pix=`<div class="pix-layout"><div class="simulation-qr"><img src="${svgImage(p.qrSvg)}" alt="QR Code do Pix"><span>PIX</span></div><div><h2>Pague com Pix.</h2><p class="muted">Use o QR Code ou copie o código abaixo para continuar.</p>${code}</div></div><button type="button" class="section-link renew-code" data-action="reload-payment" data-id="${escapeHTML(p.pedidoId)}">Gerar novo QR Code ↻</button>`;
  const card=`<h2>Dados do cartão</h2><div class="form-grid card-test-fields"><div class="field-wide"><label for="testCardName">Nome no cartão</label><input id="testCardName" name="testCardName" required minlength="3" maxlength="80" autocomplete="off" placeholder="Nome completo"></div><div class="field-wide"><label for="testCardNumber">Número do cartão</label><input id="testCardNumber" name="testCardNumber" required inputmode="numeric" pattern="[0-9 ]{16,23}" maxlength="19" autocomplete="off" placeholder="0000 0000 0000 0000"></div><div><label for="testCardExpiry">Validade (MM/AA)</label><input id="testCardExpiry" name="testCardExpiry" required inputmode="numeric" pattern="[0-9]{2}/[0-9]{2}" maxlength="5" autocomplete="off" placeholder="MM/AA"></div><div><label for="testCardCvv">CVV</label><input id="testCardCvv" name="testCardCvv" type="password" required inputmode="numeric" pattern="[0-9]{3,4}" maxlength="4" autocomplete="off" placeholder="123"></div></div><button type="button" class="section-link card-example" data-action="fill-card-example">Usar dados de exemplo</button>`;
  const boleto=`<h2>Seu boleto está pronto.</h2><div class="mock-boleto"><div class="mock-boleto-heading"><strong>GAMESRUN</strong><span>BOLETO</span></div><div class="summary-line"><span>Valor</span><strong>${money(p.valorTotal)}</strong></div><div class="summary-line"><span>Vencimento</span><strong>${escapeHTML(p.vencimento?.split('-').reverse().join('/'))}</strong></div><img class="mock-barcode" src="${svgImage(p.barrasSvg)}" alt="Código de barras do boleto"></div>${code}<button type="button" class="section-link renew-code" data-action="reload-payment" data-id="${escapeHTML(p.pedidoId)}">Gerar novo boleto ↻</button>`;
  $('#app').innerHTML=pageHeader('Pagamento.','Pedido #'+p.pedidoId+' · '+money(p.valorTotal))+`<section class="panel simulation-panel"><div class="payment-heading"><span class="eyebrow accent">FINALIZE SEU PEDIDO</span><span class="payment-method-label">${escapeHTML(method)}</span></div><form id="simulationPaymentForm" autocomplete="off">${p.formaPagamento==='pix'?pix:p.formaPagamento==='cartao'?card:boleto}<p id="simulationPaymentError" class="form-error" role="alert"></p><button class="btn btn-primary full" type="submit">${p.formaPagamento==='cartao'?'Confirmar compra':'Confirmar pagamento'} ${icon('check')}</button></form><a class="section-link back-orders" href="#pedidos">Voltar aos pedidos</a><p class="payment-environment">Ambiente de teste · utilize dados de exemplo. Nenhum valor será cobrado.</p></section>`;
}
function fillPaymentExample(){
  if(state.payment?.formaPagamento!=='cartao')return;
  $('#testCardName').value='PLAYER GAMESRUN';$('#testCardNumber').value='4242 4242 4242 4242';
  $('#testCardExpiry').value='12/'+String((new Date().getFullYear()+2)%100).padStart(2,'0');$('#testCardCvv').value='123';$('#testCardName').focus();
}
async function loadPayment(id){
  if(state.loadingPaymentId===String(id))return;
  state.loadingPaymentId=String(id);
  try{
    const data=await api(`/api/pedidos/${idPath(id)}/simulacao`,{method:'POST'});
    if(!data?.simulado||!data?.codigo)throw new Error('Não foi possível carregar os dados de pagamento.');
    state.payment=data;
    if(state.view==='pagamento')renderPayment(id);
  }catch(error){
    if(state.view==='pagamento')$('#app').innerHTML=pageHeader('Pagamento')+`<p class="notice warning">${escapeHTML(error.message)}</p><button type="button" class="btn btn-secondary" data-action="reload-payment" data-id="${escapeHTML(id)}">Tentar novamente</button>`;
  }finally{state.loadingPaymentId=null;}
}
function validateTestCard(){
  const number=$('#testCardNumber').value.replace(/\D/g,'');
  if(!['4242424242424242','5555555555554444'].includes(number))return 'Use a opção “Usar dados de exemplo” para preencher o cartão neste ambiente.';
  if($('#testCardName').value.trim().length<3)return 'Preencha o nome no cartão.';
  const expiry=$('#testCardExpiry').value.match(/^(\d{2})\/(\d{2})$/),now=new Date();
  if(!expiry||Number(expiry[1])<1||Number(expiry[1])>12||2000+Number(expiry[2])<now.getFullYear()||(2000+Number(expiry[2])===now.getFullYear()&&Number(expiry[1])<now.getMonth()+1))return 'Informe uma validade futura no formato MM/AA.';
  if(!/^\d{3,4}$/.test($('#testCardCvv').value))return 'Informe um CVV de 3 ou 4 dígitos.';
  return '';
}
async function confirmSimulation(event){
  event.preventDefault();const p=state.payment;
  if(!p||state.confirmPaymentBusy||p.status==='PAGAMENTO_SIMULADO')return;
  const error=$('#simulationPaymentError');error.textContent='';
  if(p.formaPagamento==='cartao'){const problem=validateTestCard();if(problem){error.textContent=problem;return;}}
  const button=$('button[type="submit"]',event.target);button.disabled=true;state.confirmPaymentBusy=true;
  try{
    // Não serializar o formulário: PAN, nome e CVV ficam apenas no DOM temporário.
    const order=await api(`/api/pedidos/${idPath(p.pedidoId)}/simulacao/confirmar`,{method:'POST',body:JSON.stringify({codigo:p.codigo})});
    if(order.status!=='PAGAMENTO_SIMULADO')throw new Error('Não foi possível confirmar o pagamento.');
    state.payment={...p,status:order.status};
    state.orders=[normalizeOrder(order),...state.orders.filter(item=>String(item.id)!==String(order.id))];
    event.target.reset();renderPayment(p.pedidoId);showToast('Seu pedido foi confirmado.','success','Compra confirmada');
  }catch(problem){error.textContent=problem.message;}finally{state.confirmPaymentBusy=false;button.disabled=false;}
}
async function copyPaymentCode(){
  const text=state.payment?.codigo;if(!text)return;
  try{
    if(navigator.clipboard?.writeText)await navigator.clipboard.writeText(text);
    else{const field=$('#simulationCode');field.focus();field.select();if(!document.execCommand('copy'))throw new Error('Cópia indisponível');}
    showToast('Código copiado.','success');
  }catch{const field=$('#simulationCode');field?.focus();field?.select();showToast('Selecione o código e pressione Ctrl+C para copiar.','info');}
}

function renderSeller() {
  $('#app').innerHTML=pageHeader('Passe o controle.','Seu jogo pode ser o próximo grande achado de alguém.')+`<div class="two-columns"><section class="panel form-panel"><h2>Vamos criar seu anúncio.</h2>${!state.user?'<p class="notice" style="margin-bottom:22px">Você pode preencher o anúncio agora. Entre na sua conta para publicar. <button class="section-link accent" data-action="seller-auth">Entrar →</button></p>':''}<form id="announcementForm"><div class="form-grid"><div class="field-wide"><label for="announcementTitle">Título do anúncio</label><input id="announcementTitle" name="produto" required minlength="5" maxlength="150" placeholder="Ex.: Nintendo 3DS XL azul + carregador"></div><div><label for="announcementPrice">Preço de venda (R$)</label><input id="announcementPrice" name="preco" type="number" required step="0.01" min="0.01" max="999999.99" placeholder="0,00" inputmode="decimal"></div><div><label for="announcementCategory">Categoria</label><select id="announcementCategory" name="categoriaId" required><option value="">Selecione uma categoria</option>${state.categories.map(cat=>`<option value="${escapeHTML(cat.id)}">${escapeHTML(cat.nome)}</option>`).join('')}</select></div><div class="field-wide"><label for="announcementCondition">Estado de conservação</label><select id="announcementCondition" name="estadoConservacao"><option>Novo - Lacrado</option><option>Usado - Como Novo</option><option>Usado - Bom Estado</option><option>Com Detalhes / Sucata</option></select></div><div class="field-wide"><label for="announcementImageFile">Foto do produto</label><div class="image-upload-field"><input id="announcementImageFile" type="file" accept="image/jpeg,image/png,.jpg,.jpeg,.png" aria-describedby="announcementImageHelp"><p id="announcementImageHelp" class="field-help">Escolha uma foto JPG ou PNG de até 5 MB do seu computador.</p><div id="announcementImagePreview" class="image-upload-preview" hidden></div></div><label class="image-url-label" for="announcementImage">Ou use um link de imagem</label><input id="announcementImage" name="imagemUrl" type="url" maxlength="2000" placeholder="https://seusite.com/foto.jpg" pattern="https?://.+"><p class="field-help">Se escolher um arquivo, ele será usado como foto do anúncio.</p></div><div class="field-wide"><label for="announcementDescription">Descrição</label><textarea id="announcementDescription" name="descricao" required minlength="20" maxlength="255" placeholder="Conte o que acompanha o produto, como ele funciona e os detalhes de conservação."></textarea></div></div><p id="announcementError" class="form-error" role="alert"></p><button class="btn btn-primary full" type="submit">Publicar meu anúncio ${icon('arrow')}</button></form></section><aside class="panel listing-tip"><span class="eyebrow accent">UM BOM ANÚNCIO COMEÇA AQUI</span><h3 style="margin-top:12px">Mostre o que faz seu item especial.</h3><ul><li>Use uma foto nítida e com boa iluminação.</li><li>Descreva marcas de uso e detalhes com transparência.</li><li>Informe os acessórios e jogos que acompanham.</li><li>Confira o preço e a categoria antes de publicar.</li></ul><button data-action="info" class="section-link">Como funciona ${icon('arrow')}</button></aside></div>${ownListings()}`;
}
function clearSellerImage(resetInput=true){
  if(state.sellerImagePreview)URL.revokeObjectURL(state.sellerImagePreview);
  state.sellerImageFile=null;state.sellerImagePreview='';state.sellerUploadedImage='';
  const input=$('#announcementImageFile');if(input&&resetInput)input.value='';
}
function chooseSellerImage(file){
  const error=$('#announcementError');error.textContent='';
  clearSellerImage(false);
  if(file){
    if(!/\.(jpe?g|png)$/i.test(file.name)||!['image/jpeg','image/png',''].includes(file.type)){error.textContent='Escolha uma imagem JPG ou PNG.';$('#announcementImageFile').value='';updateSellerImagePreview();return;}
    if(file.size>5*1024*1024){error.textContent='A imagem deve ter até 5 MB.';$('#announcementImageFile').value='';updateSellerImagePreview();return;}
    state.sellerImageFile=file;state.sellerImagePreview=URL.createObjectURL(file);
  }
  updateSellerImagePreview();
}
function updateSellerImagePreview(){
  const preview=$('#announcementImagePreview');if(!preview)return;
  const link=$('#announcementImage');if(link)link.disabled=Boolean(state.sellerImageFile);
  const src=state.sellerImagePreview||safeImage(link?.value,'');
  preview.hidden=!src;
  preview.innerHTML=src?`<img src="${escapeHTML(src)}" alt="Prévia da foto do produto"><div><span>${escapeHTML(state.sellerImageFile?.name||'Imagem pelo link')}</span>${state.sellerImageFile?'<button type="button" class="section-link" data-action="remove-listing-image">Remover arquivo</button>':''}</div>`:'';
}
async function handleCreateAnnouncement(event) {
  event.preventDefault();if(state.announcementBusy)return;const form=event.target;
  if(!state.user){state.sellerDraft=Object.fromEntries(new FormData(form));openAuth('login');return;}
  const error=$('#announcementError'),button=$('button[type="submit"]',form);error.textContent='';
  if(state.demo||!state.categoriesReady){error.textContent='Conecte o servidor e recarregue o catálogo antes de publicar.';return;}
  const values=Object.fromEntries(new FormData(form)),file=state.sellerImageFile;
  let imagemUrl=String(values.imagemUrl||'').trim();
  if(!file&&!safeImage(imagemUrl,'').startsWith('http')){error.textContent='Escolha uma foto do computador ou informe um link válido.';return;}
  state.announcementBusy=true;button.disabled=true;button.textContent=file?'Enviando foto…':'Publicando…';
  try {
    if(file){
      if(!state.sellerUploadedImage){
        const upload=new FormData();upload.append('arquivo',file,file.name);
        const result=await api('/api/imagens',{method:'POST',body:upload});
        if(!result?.imagemUrl||!safeImage(result.imagemUrl,''))throw new Error('O servidor não confirmou o envio da imagem.');
        if(state.sellerImageFile===file)state.sellerUploadedImage=result.imagemUrl;
        imagemUrl=result.imagemUrl;
      }else imagemUrl=state.sellerUploadedImage;
    }
    button.textContent='Publicando…';
    const saved=await api('/api/anuncios/cadastrar',{method:'POST',body:JSON.stringify({...values,imagemUrl,produto:values.produto.trim(),descricao:values.descricao.trim(),preco:Number(values.preco),categoriaId:Number(values.categoriaId),vendedorId:state.user.id})});
    if(!saved||saved.id==null)throw new Error('O servidor não confirmou a publicação.');
    state.sellerDraft={};clearSellerImage();state.products.unshift(normalizeAnnouncement(saved));navigate('produto',saved.id);showToast('Seu anúncio está no marketplace.','success','Anúncio publicado!');
  }catch(err){error.textContent=err.message;}finally{state.announcementBusy=false;button.disabled=false;button.innerHTML='Publicar meu anúncio '+icon('arrow');}
}
function normalizeOrder(order) { let items=[];try{items=Array.isArray(order.itens)?order.itens:JSON.parse(order.itensResumo || '[]');}catch{/* Resumos inválidos não impedem a lista de pedidos. */}return {...order,itens:Array.isArray(items)?items:[],total:Number(order.valorTotal ?? order.total)||0}; }
function renderOrders() {
  const orderStatus={PAGAMENTO_SIMULADO:'Pagamento confirmado',AGUARDANDO_PAGAMENTO:'Aguardando pagamento',PAGAMENTO_CONCLUIDO:'Pagamento confirmado',PAGO:'Pagamento confirmado',ENVIADO:'Enviado',ENTREGUE:'Entregue',CANCELADO:'Cancelado'};
  $('#app').innerHTML=pageHeader(state.user?`Seu espaço, ${state.user.nome.split(' ')[0]}.`:'Minha conta','Acompanhe os próximos capítulos da sua coleção.')+`<section class="orders-wrap">${!state.user?empty('Entre para ver seus pedidos.','Seus pedidos ficam associados à sua conta.','Entrar na conta','account','user'):state.accountBusy?'<div class="panel">Carregando pedidos…</div>':state.ordersError?`<p class="notice warning">${escapeHTML(state.ordersError)}</p><button data-action="reload-orders" class="btn btn-secondary" style="margin-top:15px">Tentar novamente</button>`:state.orders.length?`<div class="section-heading"><h2>Meus pedidos</h2><button data-action="reload-orders" class="section-link">Atualizar</button></div>${state.orders.map(order=>`<article class="panel order-card"><div class="order-top"><div><h2>Pedido #${escapeHTML(order.id)}</h2><small>${order.dataCriacao && !Number.isNaN(Date.parse(order.dataCriacao))?escapeHTML(new Date(order.dataCriacao).toLocaleDateString('pt-BR')):'Data não informada'}</small></div><span class="condition-label">${escapeHTML(orderStatus[order.status] || order.status || 'Pedido registrado')}</span><strong>${money(order.total)}</strong></div>${order.itens.map(item=>`<div class="order-item"><span>${escapeHTML(item.quantidade)}× ${escapeHTML(item.nome)}</span><strong>${money(Number(item.preco)*Number(item.quantidade))}</strong></div>`).join('')}<p class="field-help">${escapeHTML(order.formaPagamento || 'Forma de pagamento não informada')} · ${escapeHTML(order.enderecoEntrega || 'Endereço não informado')}</p>${order.status==='AGUARDANDO_PAGAMENTO'?`<a href="#pagamento/${idPath(order.id)}" class="btn btn-secondary order-pay">Pagar pedido ${icon('arrow')}</a>`:order.status==='PAGAMENTO_SIMULADO'?'':''}</article>`).join('')}`:empty('O primeiro play vem aí.','Você ainda não tem pedidos registrados.')} </section>`;
}
async function loadOrders() {
  if(!state.user||state.accountBusy)return;const requestedUser=state.user.id;state.accountBusy=true;if(state.view==='pedidos')renderOrders();
  try{const orders=await api(`/api/pedidos/comprador/${idPath(state.user.id)}`);if(!state.user||state.user.id!==requestedUser)return;if(!Array.isArray(orders))throw new Error('O servidor retornou uma lista de pedidos inválida.');state.orders=orders.map(normalizeOrder);state.ordersError='';}
  catch(error){if(state.user?.id===requestedUser)state.ordersError=error.message;}finally{state.accountBusy=false;if(state.view==='pedidos')renderOrders();}
}
async function refreshAccount(importGuest=false) {
  if(!state.user||state.cartBusy)return false;
  const userId=state.user.id,planKey=`gamesrun_live_v1_merge_${userId}`;
  state.cartBusy=true;state.cartSyncError='';
  try {
    const response=await api(`/api/carrinhos/usuario/${idPath(userId)}`);
    if(state.user?.id!==userId)return false;
    const entries=Array.isArray(response)?response:response?.itens;
    if(!Array.isArray(entries))throw new Error('Não foi possível carregar o carrinho da conta.');
    const remote=[];
    for(const entry of entries){
      const productId=entry.anuncioId ?? entry.anuncio?.id;
      let product=state.products.find(p=>String(p.id)===String(productId));
      if(!product){product=normalizeAnnouncement(await api(`/api/anuncios/listar/${idPath(productId)}`));state.products.push(product);}
      remote.push({...product,quantidade:Number(entry.quantidade)||1});
    }
    state.cart=cleanCart(remote);saveCart();
    // O plano guarda quantidades ABSOLUTAS antes das requisições. Se uma resposta
    // se perder, a próxima sincronização conclui o plano sem somar o visitante duas vezes.
    let plan=readStorage(planKey,[]);
    if(!Array.isArray(plan))plan=[];
    if(importGuest&&plan.length===0){
      plan=cleanCart(readStorage('gamesrun_live_v1_cart_v2_guest',[]))
        .filter(item=>!item.demo&&state.products.some(p=>String(p.id)===String(item.id)))
        .map(item=>({id:item.id,quantidade:Math.min(99,(state.cart.find(p=>String(p.id)===String(item.id))?.quantidade||0)+item.quantidade)}));
      writeStorage(planKey,plan);
    }
    for(const item of [...plan]){
      const existing=state.cart.find(p=>String(p.id)===String(item.id));
      const endpoint=`/api/carrinhos/usuario/${idPath(userId)}/itens`;
      if(existing){
        if(existing.quantidade!==item.quantidade)await api(`${endpoint}/${idPath(item.id)}?quantidade=${item.quantidade}`,{method:'PUT'});
        existing.quantidade=item.quantidade;
      }else{
        await api(endpoint,{method:'POST',body:JSON.stringify({anuncioId:item.id,quantidade:item.quantidade})});
        state.cart.push({...state.products.find(p=>String(p.id)===String(item.id)),quantidade:item.quantidade});
      }
      const guest=cleanCart(readStorage('gamesrun_live_v1_cart_v2_guest',[])).filter(p=>String(p.id)!==String(item.id));
      writeStorage('gamesrun_live_v1_cart_v2_guest',guest);
      plan=plan.filter(p=>String(p.id)!==String(item.id));writeStorage(planKey,plan);saveCart();
    }
    return true;
  }catch(error){state.cartSyncError='Não foi possível sincronizar seu carrinho. '+error.message;showToast(error.message,'error','Confira seu carrinho');return false;}
  finally{state.cartBusy=false;await loadOrders();}
}
let authOpener=null;
function openAuth(mode='login',after=null) { authOpener=document.activeElement;state.afterAuth=after;switchAuthMode(mode);if(!$('#authModal').open)$('#authModal').showModal();requestAnimationFrame(()=>$(mode==='signup'?'#signupNome':'#loginEmail').focus()); }
function closeAuth() { $('#authModal').close();state.afterAuth=null;authOpener?.focus(); }
function switchAuthMode(mode) {
  const signup=mode==='signup';$('#loginForm').hidden=signup;$('#signupForm').hidden=!signup;$('#authModalTitle').textContent=signup?'Seu primeiro play por aqui.':'Bom ter você de volta.';$('#authSubtitle').textContent=signup?'Crie sua conta e continue o jogo.':'Entre para continuar sua jornada.';
  $('#loginTab').setAttribute('aria-selected',String(!signup));$('#signupTab').setAttribute('aria-selected',String(signup));$('#loginTab').tabIndex=signup?-1:0;$('#signupTab').tabIndex=signup?0:-1;
  $('#loginError').textContent='';$('#signupError').textContent='';
}
function validCPF(value) { const digits=value.replace(/\D/g,'');if(digits.length!==11||/^(\d)\1+$/.test(digits))return false;for(let n=9;n<11;n++){let sum=0;for(let i=0;i<n;i++)sum+=Number(digits[i])*(n+1-i);const digit=(sum*10)%11;if((digit===10?0:digit)!==Number(digits[n]))return false;}return true; }
async function handleAuth(event,signup=false) {
  event.preventDefault();const form=event.target,button=$('button[type="submit"]',form),error=$(signup?'#signupError':'#loginError');
  const email=$(signup?'#signupEmail':'#loginEmail').value.trim(),senha=$(signup?'#signupPassword':'#loginPassword').value;
  const payload=signup?{nome:$('#signupNome').value.trim(),nick:email,cpf:$('#signupCpf').value.replace(/\D/g,''),idade:Number($('#signupIdade').value),senha}:{nick:email,senha};
  if(signup&&(!Number.isInteger(payload.idade)||payload.idade<1||payload.idade>120)){error.textContent='Informe uma idade válida.';return;}
  if(signup&&!validCPF(payload.cpf)){error.textContent='Confira o CPF informado.';return;}
  error.textContent='';button.disabled=true;const old=button.innerHTML;button.textContent=signup?'Criando sua conta…':'Entrando…';
  // Impede fechar ou alternar formulários enquanto uma autenticação está em andamento.
  $$('[data-action="auth-mode"], [data-action="close-auth"]', $('#authModal')).forEach(el=>el.disabled=true);
  try {
    const result=await api(signup?'/api/usuarios/cadastrar':'/api/usuarios/login',{method:'POST',body:JSON.stringify(payload)}),user=cleanUser(result);
    if(!user)throw new Error('O servidor não confirmou a conta.');
    clearFavorites();state.cartSyncError='';state.user=user;state.cart=cleanCart(readStorage(cartKey(),[]));state.orders=[];writeStorage('gamesrun_live_v1_session_v2',user,sessionStorage);updateHeader();
    const continuation=state.afterAuth;$('#authModal').close();state.afterAuth=null;form.reset();
    showToast(signup?'Sua conta foi criada.':`Você entrou, ${user.nome.split(' ')[0]}!`,'success',signup?'Sucesso':'Bem-vindo de volta');
    if(!state.demo){await loadFavorites();await refreshAccount(true);}
    if(continuation)await continuation();else renderRoute();
  }catch(err){error.textContent=err.message;}finally{button.disabled=false;button.innerHTML=old;$$('[data-action="auth-mode"], [data-action="close-auth"]', $('#authModal')).forEach(el=>el.disabled=false);}
}
async function logout() { if(state.checkoutBusy||state.cartBusy||state.announcementBusy){showToast('Aguarde a operação em andamento antes de sair.','info');return;}try{await api('/api/usuarios/logout',{method:'POST'});}catch(error){showToast(error.message,'error');return;}state.payment=null;clearSellerImage();state.sellerDraft={};state.checkoutDraft={endereco:'',payment:'pix'};state.cartSyncError='';state.user=null;clearFavorites();state.filters.favorites=false;state.orders=[];state.ordersError='';try{sessionStorage.removeItem('gamesrun_live_v1_session_v2');}catch{}state.cart=cleanCart(readStorage('gamesrun_live_v1_cart_v2_guest',[]));updateHeader();navigate('home');showToast('Você saiu da sua conta.','info'); }
function renderChat() {
  $('#app').innerHTML=pageHeader('Jogue seguro.','Um espaço para reconhecer sinais comuns de golpes.')+`<section class="panel chat-demo"><p class="notice warning">Assistente demonstrativo: orientações locais, sem mensagens enviadas a vendedores e sem análise por IA.</p><div class="chat-messages" id="chatMessages" role="log" aria-live="polite"><div class="chat-bubble system">Recebeu uma proposta suspeita? Cole a mensagem aqui para identificar sinais comuns de risco. Não compartilhe senhas nem dados de pagamento.</div>${state.chat.map(msg=>`<div class="chat-bubble ${msg.system?'system':''}">${escapeHTML(msg.text)}</div>`).join('')}</div><form id="chatForm" class="chat-form"><label for="chatInput" class="sr-only">Mensagem para analisar</label><input id="chatInput" required maxlength="1500" placeholder="Ex.: pediram um Pix por fora…"><button class="btn btn-primary" type="submit">Analisar ${icon('arrow')}</button></form></section>`;
  $('#chatMessages').scrollTop=$('#chatMessages').scrollHeight;
}
function handleChat(event) { event.preventDefault();const text=$('#chatInput').value.trim();if(!text)return;state.chat.push({text});const risky=/pix|por fora|whatsapp|deposit|taxa|urgente|senha|codigo|código|link|transfer/i.test(text);state.chat.push({system:true,text:risky?'A mensagem contém termos que merecem atenção. Não compartilhe códigos ou senhas, confira o destinatário e desconfie de taxas extras e pagamentos solicitados fora do fluxo da compra. Essa verificação por palavras não comprova um golpe.':'Não encontrei os termos de atenção deste exemplo. Isso não confirma que a proposta é segura. Confira produto, vendedor, destinatário e condições antes de prosseguir.'});renderChat();$('#chatInput').focus(); }
// Aviso inspirado no card enviado, de akshat-patel28 / Uiverse.io.
// Ele só informa sucesso de conta depois de uma resposta válida da API.
function showToast(message,type='info',title) {
  const container=$('#toastContainer');if(container.children.length>=4)container.firstElementChild.remove();
  const toast=document.createElement('div');toast.className=`toast ${type}`;
  toast.innerHTML=`<span class="toast-icon">${icon(type==='success'?'check':'info')}</span><div class="toast-message"><strong>${escapeHTML(title || (type==='error'?'Algo deu errado':type==='success'?'Tudo certo!':'GamesRun'))}</strong><p>${escapeHTML(message)}</p></div><button aria-label="Fechar aviso">${icon('close')}</button>`;
  $('button',toast).addEventListener('click',()=>toast.remove());container.appendChild(toast);setTimeout(()=>toast.remove(),type==='error'?10000:6500);
}
function clearFavorites() {
  state.favoritesVersion++;state.favorites=[];state.favoritesReady=false;state.favoritesBusy=false;
}
async function loadFavorites() {
  if(!state.user)return false;
  const userId=state.user.id,version=state.favoritesVersion;
  try {
    const ids=await api('/api/favoritos');
    if(state.user?.id!==userId||state.favoritesVersion!==version)return false;
    if(!Array.isArray(ids))throw new Error('O servidor retornou uma lista de favoritos inválida.');
    state.favorites=[...new Set(ids.map(String))];state.favoritesReady=true;updateHeader();return true;
  }catch(error){
    if(state.user?.id===userId&&state.favoritesVersion===version){state.favoritesReady=false;showToast('Não foi possível carregar seus favoritos. '+error.message,'error');}
    return false;
  }
}
async function toggleFavorite(id) {
  const key=String(id);
  if(!state.user){openAuth('login',()=>toggleFavorite(key));return;}
  if(state.favoritesBusy)return;
  const userId=state.user.id,version=state.favoritesVersion;
  state.favoritesBusy=true;
  try {
    if(!state.favoritesReady&&!await loadFavorites())return;
    if(state.user?.id!==userId||state.favoritesVersion!==version)return;
    const product=state.products.find(p=>String(p.id)===key);
    if(!product||product.demo){showToast('Salve favoritos de anúncios publicados no servidor.','info');return;}
    const removing=state.favorites.includes(key);
    await api(`/api/favoritos/${idPath(key)}`,{method:removing?'DELETE':'PUT'});
    if(state.user?.id!==userId||state.favoritesVersion!==version)return;
    state.favorites=removing?state.favorites.filter(x=>x!==key):[...state.favorites,key];
    updateHeader();if(state.view==='home')renderCatalog();else if(state.view==='produto')renderRoute();
  }catch(error){
    if(state.user?.id===userId&&state.favoritesVersion===version){
      showToast(error.message,'error');
      if(error.status===401){clearFavorites();updateHeader();if(state.view==='home')renderCatalog();else if(state.view==='produto')renderRoute();openAuth();}
    }
  }finally{if(state.favoritesVersion===version)state.favoritesBusy=false;}
}
async function handleAction(event) {
  const target=event.target.closest('[data-action]');if(!target||target.disabled)return;
  const {action,id,value}=target.dataset;
  switch(action) {
    case 'retry-app':await initApp();break;
    case 'seller':navigate('vendedor');break;
    case 'sync-cart':await refreshAccount(true);renderRoute();break;
    case 'explore':$('#catalog')?.scrollIntoView({behavior:'smooth'});break;
    case 'reset':resetFilters();break;
    case 'all':state.filters.condition='';state.filters.favorites=false;showCatalog(false);break;
    case 'condition':state.filters.condition=state.filters.condition===value?'':value;showCatalog(false);break;
    case 'platform':state.filters.platform=value;state.filters.category='';state.filters.query='';showCatalog();break;
    case 'category':state.filters.category=state.filters.category===id?'':id;state.filters.platform='';showCatalog();break;
    case 'favorites':state.filters.favorites=!state.filters.favorites;showCatalog();break;
    case 'favorite':await toggleFavorite(id);break;
    case 'more':state.limit+=8;renderCatalog();break;
    case 'add-cart':target.disabled=true;await addToCart(id);target.disabled=false;break;
    case 'buy':target.disabled=true;if(await addToCart(id))navigate('carrinho');target.disabled=false;break;
    case 'quantity':await changeQuantity(id,Number(target.dataset.delta));break;
    case 'remove':await changeQuantity(id,0,true);break;
    case 'account':if(state.user){navigate('pedidos');await loadOrders();}else openAuth();break;
    case 'checkout-auth':state.checkoutDraft.endereco=$('#checkoutAddress')?.value||'';openAuth('login',()=>navigate('checkout'));break;
    case 'seller-auth':openAuth();break;
    case 'close-auth':closeAuth();break;
    case 'auth-mode':switchAuthMode(value);$(value==='signup'?'#signupNome':'#loginEmail').focus();break;
    case 'password':{const input=document.getElementById(target.dataset.target),show=input.type==='password';input.type=show?'text':'password';target.setAttribute('aria-label',show?'Ocultar senha':'Mostrar senha');target.setAttribute('aria-pressed',String(show));break;}
    case 'logout':await logout();break;
    case 'delete-listing':await deleteListing(id);break;
    case 'reload-payment':await loadPayment(id);break;
    case 'copy-payment':await copyPaymentCode();break;
    case 'fill-card-example':fillPaymentExample();break;
    case 'remove-listing-image':if(!state.announcementBusy){clearSellerImage();updateSellerImagePreview();}break;
    case 'info':$('#infoModal').showModal();break;
    case 'close-info':$('#infoModal').close();break;
    case 'reload-orders':await loadOrders();break;
  }
}
document.addEventListener('click',handleAction);
document.addEventListener('submit',event=>{
  const id=event.target.id;
  if(id==='searchForm'){event.preventDefault();state.filters.query=$('#searchInput').value.trim();showCatalog();}
  if(id==='loginForm')handleAuth(event,false);
  if(id==='signupForm')handleAuth(event,true);
  if(id==='checkoutForm')executeCheckout(event);
  if(id==='announcementForm')handleCreateAnnouncement(event);
  if(id==='chatForm')handleChat(event);
  if(id==='simulationPaymentForm')confirmSimulation(event);
});
document.addEventListener('change',event=>{
  if(event.target.id==='announcementImageFile'&&!state.announcementBusy)chooseSellerImage(event.target.files?.[0]);
  if(event.target.id==='announcementImage')updateSellerImagePreview();
  if(event.target.closest('#announcementForm')&&event.target.name)state.sellerDraft[event.target.name]=event.target.value;
  if(event.target.name==='payment')state.checkoutDraft.payment=event.target.value;
  if(event.target.id==='sortProducts'){state.filters.sort=event.target.value;renderCatalog();}
  if(event.target.id==='priceFilter'){state.filters.price=event.target.value;state.limit=8;renderCatalog();}
  if(event.target.name==='payment')updatePaymentExplanation(event.target.value);
});
document.addEventListener('input',event=>{if(event.target.id==='announcementImage')updateSellerImagePreview();if(event.target.id==='testCardNumber')event.target.value=event.target.value.replace(/\D/g,'').slice(0,16).replace(/(\d{4})(?=\d)/g,'$1 ');if(event.target.id==='testCardExpiry')event.target.value=event.target.value.replace(/\D/g,'').slice(0,4).replace(/(\d{2})(\d)/,'$1/$2');if(event.target.closest('#announcementForm')&&event.target.name)state.sellerDraft[event.target.name]=event.target.value;if(event.target.id==='checkoutAddress')state.checkoutDraft.endereco=event.target.value;if(event.target.id==='signupCpf'){const digits=event.target.value.replace(/\D/g,'').slice(0,11);event.target.value=digits.replace(/(\d{3})(\d)/,'$1.$2').replace(/(\d{3})(\d)/,'$1.$2').replace(/(\d{3})(\d{1,2})$/,'$1-$2');}});
document.addEventListener('error',event=>{if(event.target.tagName==='IMG'&&!event.target.dataset.fallbackTried){event.target.dataset.fallbackTried='true';event.target.src=safeImage('assets/controller.svg');}},true);
$('#authModal').addEventListener('cancel',event=>{if($('button[type="submit"]:disabled',$('#authModal')))event.preventDefault();else state.afterAuth=null;});
$('#authModal').addEventListener('close',()=>{if(authOpener?.isConnected)authOpener.focus();});
$('.auth-tabs').addEventListener('keydown',event=>{if(!['ArrowLeft','ArrowRight','Home','End'].includes(event.key))return;event.preventDefault();const signup=event.key==='End'||(event.key!=='Home'&&$('#loginTab').getAttribute('aria-selected')==='true');if($('#signupTab').disabled)return;switchAuthMode(signup?'signup':'login');$(signup?'#signupTab':'#loginTab').focus();});
window.addEventListener('hashchange',()=>{renderRoute();window.scrollTo({top:0,behavior:'instant'});$('#app').focus({preventScroll:true});});
initApp();

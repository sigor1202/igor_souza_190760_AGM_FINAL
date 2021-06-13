package com.igor.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

public class Game extends ApplicationAdapter {
	//cria as variaveis e a lista
	private float larguraDispositivo;
	private float alturaDispositivo;
	private float posicaoInicialVerticalPassaro = 0;
	private float variacao = 0;
	private float posicaoCanoHorizontal;
	private float posicaoCanoVertical;
	private float posicaoHorizontalPassaro=0;
	private float espacoEntreCanos;

	private int pontos = 0;
	private int pontuacaoMaxima=0;
	private int estadoJogo =0;
	private int randonMoedaP;
	private int randonMoedaD;
	private float gravidade = 0;

	private Texture[] passaros;
	private Texture fundo;
	private Texture canoBaixo;
	private Texture canoTopo;
	private Texture gameOver;
	SpriteBatch batch;

	private boolean passouCano = false;
	private Random random;
	private ShapeRenderer shapeRenderer;
	//cria o formato dos colliders
	private Circle circuloPassaro;
	private Rectangle retanguloCanoCima;
	private Rectangle retanguloCanoBaixo;

	//cria os BitMaps
	BitmapFont textoPontuacao;
	BitmapFont textoReiniciar;
	BitmapFont textoMelhorPontuacao;

	//variaveis para os sons
	Sound somVoando;
	Sound somColisao;
	Sound somPontuacao;

	//cria a variavel de preferencias
	Preferences preferencias;

	//variaveis pras moedas
	private Random randomMoedaPrata;
	private Random randomMoedaDourada;
	private Texture moedaPrata;
	private Texture moedaDourada;
	private Texture inicio;
	private Circle circuloD;
	private Circle circuloP;
	private float posicaoMoedaDouradaHorizontal;
	private float posicaoMoedaPrataHorizontal;
	boolean randomizarMoedaPrata =true;
	boolean randomizarMoedaDourada =true;
	Sound somMoeda;

	@Override
	//classe semelhante ao onCreate
	public void create () {

		inicializaTexturas();
		inicializaObjetos();


	}

	private void inicializaObjetos() {
		random = new Random();
		randomMoedaPrata = new Random();
		randomMoedaDourada = new Random();
		batch = new SpriteBatch();

		//pega a altura e largura da tela
		larguraDispositivo = Gdx.graphics.getWidth();
		alturaDispositivo = Gdx.graphics.getHeight();
		//seta a altura do passaro na vertical
		posicaoInicialVerticalPassaro = alturaDispositivo/2;
		posicaoCanoHorizontal = larguraDispositivo;
		posicaoMoedaDouradaHorizontal = larguraDispositivo+larguraDispositivo/2;
		posicaoMoedaPrataHorizontal = larguraDispositivo+larguraDispositivo/2;

		//seta o espaço entre os canas
		espacoEntreCanos = 300;

		//cria e configura e texto da pontuação
		textoPontuacao = new BitmapFont();
		textoPontuacao.setColor(Color.WHITE);
		textoPontuacao.getData().setScale(10);
		//cria e configura e texto de reiniciar
		textoReiniciar = new BitmapFont();
		textoReiniciar.setColor(Color.GREEN);
		textoReiniciar.getData().setScale(2);
		//cria e configura e texto melhor pontuação
		textoMelhorPontuacao = new BitmapFont();
		textoMelhorPontuacao.setColor(Color.RED);
		textoMelhorPontuacao.getData().setScale(2);
		//cria os objetos para o collider
		shapeRenderer = new ShapeRenderer();
		circuloPassaro = new Circle();
		retanguloCanoCima = new Rectangle();
		retanguloCanoBaixo = new Rectangle();
		circuloD = new Circle();
		circuloP = new Circle();
		//seta os sons nas variaveis
		somVoando= Gdx.audio.newSound(Gdx.files.internal("som_asa.wav"));
		somColisao = Gdx.audio.newSound(Gdx.files.internal("som_batida.wav"));
		somPontuacao = Gdx.audio.newSound(Gdx.files.internal("som_pontos.wav"));
		somMoeda = Gdx.audio.newSound(Gdx.files.internal("Som_Moeda.wav"));
		//configura as preferencias
		preferencias = Gdx.app.getPreferences("flappyBird");
		pontuacaoMaxima = preferencias.getInteger("pontuacaoMaxima",0);

	}

	private void inicializaTexturas() {
		//cria uma lista de objetos passaros e atribui os tres a ela
		passaros = new Texture[3];
		passaros[0] = new Texture("red01.png");
		passaros[1] = new Texture("red02.png");
		passaros[2] = new Texture("red03.png");
		//pega a imagem fundo e atribui a variavel
		fundo = new Texture("fundo.png");
		//pega as texturas dodos canos e atreibui a variavel
		canoBaixo = new Texture("cano_baixo_maior.png");
		canoTopo = new Texture("cano_topo_maior.png");
		gameOver = new Texture("game_over.png");

		moedaDourada = new Texture("moeda_dourada.png");
		moedaPrata = new Texture("moeda_prata.png");
		inicio = new Texture("flappy-bird-logo.png");
	}

	@Override
	//renderiza o conteudo
	public void render () {

		verificaEstadojogo();
		moeda();
		desenharTexturas();
		detectarColisao();
		validaPontos();


	}

	private void detectarColisao() {
		//configura os colliders do passaro e dos canos
		circuloPassaro.set(50 + passaros[0].getWidth()/2,
				posicaoInicialVerticalPassaro+passaros[0].getHeight()/2,passaros[0].getHeight()/2);

		retanguloCanoBaixo.set(posicaoCanoHorizontal,
				alturaDispositivo/2-canoBaixo.getHeight()-espacoEntreCanos/2+posicaoCanoVertical,canoBaixo.getWidth(),
				canoBaixo.getHeight());

		retanguloCanoCima.set(posicaoCanoHorizontal,
				alturaDispositivo/2 + espacoEntreCanos/2 + posicaoCanoVertical,
				canoTopo.getWidth(),canoTopo.getHeight());

		//collider moedas

		circuloP.set(posicaoMoedaPrataHorizontal,alturaDispositivo/2 ,moedaPrata.getHeight());
		circuloD.set(posicaoMoedaDouradaHorizontal, alturaDispositivo/2,moedaDourada.getHeight());
		//cria os booleans para verificar a colisão
		boolean bateuCanoCima = Intersector.overlaps(circuloPassaro,retanguloCanoCima);
		boolean bateuCanoBaixo = Intersector.overlaps(circuloPassaro,retanguloCanoBaixo);
		boolean moedaPrata = Intersector.overlaps(circuloPassaro,circuloP);
		boolean moedaDourada = Intersector.overlaps(circuloPassaro,circuloD);

		//verifica se bateu
		if(bateuCanoBaixo || bateuCanoCima){
			if(estadoJogo==1){
				somColisao.play();
				estadoJogo=2;
			}
		}

		//verifica se colidiu com a moeda prata
		if(moedaPrata)
		{
			//da play no som
			somMoeda.play();
			//adiciona mais 5 nos pontos
			pontos = pontos+5;
			//retorna o boolean da colisão para faslso
			moedaPrata=false;
			//seta a condição de randomizar a moeda prata como true
			randomizarMoedaPrata =true;

		}

		//verifica se colidiu com a moeda Dourada
		if(moedaDourada)
		{
			//da play no som
			somMoeda.play();
			//adiciona mais 10 nos pontos
			pontos=pontos+10;
			//retorna o boolean da colisão para faslso
			moedaDourada=false;
			//seta a condição de randomizar s moeda dourada como true
			randomizarMoedaDourada = true;
		}
	}

	//verifica se passou do cano se sim adiciona mais um ao pontos
	private void validaPontos() {
		if (posicaoCanoHorizontal<50-passaros[0].getWidth())
		{
			if(!passouCano)
			{
				pontos++;
				passouCano = true;
				somPontuacao.play();
			}
		}

		variacao += Gdx.graphics.getDeltaTime() * 10;
		//verifica se a variação e se for maior que tres iguala a zero
		if(variacao > 3)
			variacao = 0;
	}

	private void moeda(){
		//se o estado do jogo for iguaal a 1 aas moedas começam a se mover
		if(estadoJogo==1)
		{
			posicaoMoedaPrataHorizontal -= Gdx.graphics.getDeltaTime()*400;
			posicaoMoedaDouradaHorizontal -= Gdx.graphics.getDeltaTime()*400;
		}
		//randomiza e guarda em uma variavel o valor para usar na posição da moeda quando a condição for cumprida
		randonMoedaP = randomMoedaPrata.nextInt(8)+2;
		randonMoedaD = randomMoedaDourada.nextInt(12)+2;

		//se a condição para randomizar a moeda prata for true, randomiza e seta como false novamente
		if(randomizarMoedaPrata)
		{
			posicaoMoedaPrataHorizontal = larguraDispositivo/2+larguraDispositivo * randonMoedaP;
			randomizarMoedaPrata =false;
		}

		//se a condição para randomizar a moeda doura for true, randomiza e seta como false novamente
		if(randomizarMoedaDourada)
		{
			posicaoMoedaDouradaHorizontal = larguraDispositivo/2+larguraDispositivo * randonMoedaD;
			randomizarMoedaDourada =false;
		}

		//chama a função que valida a corrigi a popsição das moedas para evitar problemas
		validaPosicao();

	}

	private void validaPosicao()
	{
		/*se a posição horizontal da moeda for menor que a largura dela randomiza a posição novamente
		para evitar que a moeda pare de aparecer se o player não pegar a moeda prata*/
		if(posicaoMoedaPrataHorizontal<-moedaPrata.getWidth())
		{
			posicaoMoedaPrataHorizontal = larguraDispositivo/2 + larguraDispositivo * randonMoedaP;
		}

		/*se a posição horizontal da moeda for menor que a largura dela randomiza a posição novamente
		para evitar que a moeda pare de aparecer se o player não pegar a moeda dourada*/
		if(posicaoMoedaDouradaHorizontal<-moedaPrata.getWidth())
		{
			posicaoMoedaDouradaHorizontal = larguraDispositivo/2 + larguraDispositivo * randonMoedaD;
		}

		//verifica se a posição da moeda prata é igual a do cano e se for joga ela 100 pixels para frente
		if(posicaoMoedaPrataHorizontal == posicaoCanoHorizontal)
		{
			posicaoMoedaPrataHorizontal = posicaoMoedaPrataHorizontal +100;
		}

		//verifica se a posição da moeda dourada é igual a do cano e se for joga ela 100 pixels para frente
		if(posicaoMoedaDouradaHorizontal == posicaoCanoHorizontal)
		{
			posicaoMoedaDouradaHorizontal = posicaoMoedaPrataHorizontal +100;
		}

		//verifica se a posição das duas moedas é igual se sim joga a moda para frente
		if(posicaoMoedaDouradaHorizontal == posicaoMoedaPrataHorizontal)
		{
			posicaoMoedaDouradaHorizontal = posicaoMoedaPrataHorizontal +larguraDispositivo;
		}
	}


	private void verificaEstadojogo() {

		//verificação do clique na tela
		boolean toquTela = Gdx.input.justTouched();
		//se o estado do jogo for 0
		if(estadoJogo==0){

			//se for clicado subtrai 15 da gravidade
			if(toquTela)
			{
				gravidade = -15;
				estadoJogo =1;
				somVoando.play();
			}
			//se o estado do jogo for 1
		}else if(estadoJogo ==1){

			//se flor clicado subtrai 25 da gravidade
			if(toquTela)
			{
				gravidade = -15;
				somVoando.play();
			}
			//faz o cano se mover
			posicaoCanoHorizontal -= Gdx.graphics.getDeltaTime()*400;
			if (posicaoCanoHorizontal < - canoTopo.getWidth()){

				posicaoCanoHorizontal = larguraDispositivo;

				posicaoCanoVertical = random.nextInt(400)-200;
				passouCano = false;
			}

			//verifica se a posição vertical é maior que zero ou clicou na tela
			if (posicaoInicialVerticalPassaro > 0 || toquTela) {
				//atualiza a posição vertical
				posicaoInicialVerticalPassaro = posicaoInicialVerticalPassaro - gravidade;
			}

			//adiciona +1 nas variaveis
			gravidade++;

		}else if(estadoJogo ==2){
			//se os pontos for maior que a pontuação maxima
			if(pontos>pontuacaoMaxima){
				//iguala a pontuação maxima aos pontos
				pontuacaoMaxima = pontos;
				//retorna o valor da pontuação maxima as preferencias
				preferencias.putInteger("pontuacaoMaxima",pontuacaoMaxima);
			}

			posicaoHorizontalPassaro -= Gdx.graphics.getDeltaTime()*500;

			if(toquTela){
				estadoJogo=0;
				pontos = 0;
				gravidade=0;
				posicaoHorizontalPassaro =0;
				posicaoInicialVerticalPassaro = alturaDispositivo/2;
				posicaoCanoHorizontal = larguraDispositivo;
				posicaoMoedaPrataHorizontal =   larguraDispositivo/2 + larguraDispositivo * randonMoedaP;
				posicaoMoedaDouradaHorizontal =   larguraDispositivo/2 + larguraDispositivo * randonMoedaD;
			}

		}

	}


	private void desenharTexturas() {
		batch.begin();

		//desenha e configura o objeto na tela
		batch.draw(fundo, 0,0,larguraDispositivo,alturaDispositivo);
		batch.draw(passaros[(int) variacao],50+posicaoHorizontalPassaro,posicaoInicialVerticalPassaro);
		batch.draw(canoBaixo, posicaoCanoHorizontal, alturaDispositivo/2- canoBaixo.getHeight()-espacoEntreCanos/2+ posicaoCanoVertical);
		batch.draw(canoTopo, posicaoCanoHorizontal, alturaDispositivo/2 + espacoEntreCanos/2 + posicaoCanoVertical);
		//moeda
		batch.draw(moedaDourada, posicaoMoedaDouradaHorizontal ,alturaDispositivo/2 );
		batch.draw(moedaPrata, posicaoMoedaPrataHorizontal ,alturaDispositivo/2 );
		textoPontuacao.draw(batch, String.valueOf(pontos), larguraDispositivo/2,alturaDispositivo-100);

		//se o estado deo jogo for igual a 2
		if(estadoJogo==2){
			//desnha na tela o game over
			batch.draw(gameOver,larguraDispositivo/2 - gameOver.getWidth()/2,alturaDispositivo/2);
			//desenha na teala a frase TOQUE NA TELA PARA REINICIAR
			textoReiniciar.draw(batch,"TOQUE NA TELA PARA REINICIAR!",larguraDispositivo/2 - 250,alturaDispositivo/2-gameOver.getHeight()/2);
			// desenha a frase da melhor pontuação e concatena o valor
			textoMelhorPontuacao.draw(batch,"SUA MELHOR PONTUAÇÃO É :"+pontuacaoMaxima+ " PONTOS",larguraDispositivo/2 - 300 ,alturaDispositivo/2-gameOver.getHeight()*2);
		}
		if(estadoJogo ==0){
			batch.draw(inicio, larguraDispositivo / 2 - inicio.getWidth()/2, alturaDispositivo / 2);
		}


		batch.end();
	}

	@Override
	public void dispose () {

	}
}

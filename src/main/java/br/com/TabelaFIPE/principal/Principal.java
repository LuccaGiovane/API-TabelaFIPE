package br.com.TabelaFIPE.principal;

import br.com.TabelaFIPE.model.Dados;
import br.com.TabelaFIPE.model.Modelos;
import br.com.TabelaFIPE.model.Veiculo;
import br.com.TabelaFIPE.service.ConsumoApi;
import br.com.TabelaFIPE.service.ConverteDados;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal
{
    private Scanner input = new Scanner(System.in);
    private ConsumoApi consumoApi = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();

    private final String URL_BASE = "https://parallelum.com.br/fipe/api/v1/";

    public void exibeMenu()
    {
        List<Veiculo> veiculos = new ArrayList<>();
        String endereco;


        var menu =
                """
                ========== TABELA FIPE ==========    
                
                === Opções de Consulta ===
                [Carro]
                [Moto]
                [Caminhão]
                    
                Digite o tipo de veículo para a consulta: 
                """;
        System.out.println(menu);
        var opcao = input.nextLine();

        if(opcao.toLowerCase().contains("carr"))
        {
            endereco = URL_BASE + "carros/marcas";
        }
        else if (opcao.toLowerCase().contains("mot"))
        {
            endereco = URL_BASE + "motos/marcas";
        }
        else
        {
            endereco = URL_BASE + "caminhoes/marcas";
        }

        var json = consumoApi.obterDados(endereco);
        System.out.println(json);

        var marcas = conversor.obterLista(json, Dados.class);

        marcas.stream()
                .sorted(Comparator.comparing(Dados::codigo))//ordena a lista de marcas por codigo
                .forEach(System.out::println);//imprime as marcas de carros


        //Faz a pesquisa da marca desejada
        System.out.println("Infome o código da marca para a consulta:");
        var codigoMarca = input.nextLine();

        endereco = endereco + "/" + codigoMarca + "/modelos";
        json = consumoApi.obterDados(endereco);
        var modeloLista = conversor.obterDados(json, Modelos.class);//como modelo ja esta como lista usa o obter dados
        System.out.println("=== Modelos da Marca ===");
        modeloLista.modelos().stream()
                .sorted(Comparator.comparing(Dados::codigo))
                .forEach(System.out::println);

        //Faz a pesquisa do modelo desejado
        System.out.println("\nDigite o nome do carro desejado:");
        var nomeVeiculo = input.nextLine();
        //pega a lista de modelos filtrados pela marca, filtra todomundo que tenha o nome
        //parecido com o digitado e joga numa nova lista
        List<Dados> modelosFiltrados = modeloLista.modelos().stream()
                .filter(m -> m.nome().toLowerCase().contains(nomeVeiculo.toLowerCase()))
                .collect(Collectors.toList());
        System.out.println("\n=== Modelos Filtrados ===");
        modelosFiltrados.forEach(System.out::println);

        //Faz a pesquisa do modelo específico desejado
        System.out.println("Digite o código do modelo desejado:");
        var codigoModelo = input.nextLine();

        endereco = endereco + "/" + codigoModelo + "/anos";
        json = consumoApi.obterDados(endereco);
        List<Dados> anos = conversor.obterLista(json, Dados.class);

        //Adiciona todos os veiculos selecionados em uma lista de veiculos
        for(int i=0; i< anos.size(); i++)
        {
            //vai iterando nos anos dos modelos selecionados
            var enderecoAnos = endereco + "/" + anos.get(i).codigo();
            json = consumoApi.obterDados(enderecoAnos);
            Veiculo veiculo = conversor.obterDados(json, Veiculo.class);
            veiculos.add(veiculo);

        }

        System.out.println("\n=== Veiculos Filtrados ===");
        veiculos.forEach(System.out::println);

    }
}
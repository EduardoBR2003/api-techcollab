package br.com.api_techcollab.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        // Cria uma instância do ModelMapper
        ModelMapper modelMapper = new ModelMapper();

        // Você pode adicionar configurações personalizadas aqui se necessário no futuro.
        // Por enquanto, a instância padrão é suficiente, pois resolveremos o problema
        // da coleção lazy na camada de serviço.

        return modelMapper;
    }
}
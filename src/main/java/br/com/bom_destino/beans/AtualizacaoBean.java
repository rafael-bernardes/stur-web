package br.com.bom_destino.beans;

import java.io.IOException;
import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

import br.com.bom_destino.utils.PropertiesUtil;


@ManagedBean(name = "atualizacao")
public class AtualizacaoBean implements Serializable {

	private static final String FALHA_AO_ATUALIZAR_DADOS_GEOGRAFICOS = "Falha ao atualizar dados geográficos";

	private static final String TEXTO_SATELITE = "SATELITE";

	private static final String TEXTO_IBGE = "IBGE";

	private static final long serialVersionUID = 1L;

	private boolean ibge;
	private boolean satelite;
	
	public void atualizarDados() {
		ResteasyClient client = new ResteasyClientBuilder().build();
		
		try {
			WebTarget target = client.target(PropertiesUtil.obterURI("gateway-api")).path("dados-geograficos-stur");
			
			StringBuilder corpo = new StringBuilder();
			
			if(!ibge && !satelite) {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Informe a(s) fonte(s) desejada(s).", ""));
				return;
			}else if(ibge && satelite) {
				corpo.append(TEXTO_IBGE);
				corpo.append("|");
				corpo.append(TEXTO_SATELITE);
			}else if(ibge) {
				corpo.append(TEXTO_IBGE);
			}else if(satelite) {
				corpo.append(TEXTO_SATELITE);
			}
			
			Response response = target.request().post(Entity.entity(corpo.toString(), MediaType.APPLICATION_JSON));
			
			if(!Family.SUCCESSFUL.equals(response.getStatusInfo().getFamily())) {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, FALHA_AO_ATUALIZAR_DADOS_GEOGRAFICOS, ""));
			}else {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Dados geográficos atualizados com sucesso", ""));
				
				ibge = false;
				satelite = false;
			}
			
		} catch (IllegalArgumentException e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, FALHA_AO_ATUALIZAR_DADOS_GEOGRAFICOS, e.getMessage()));
		} catch (NullPointerException e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, FALHA_AO_ATUALIZAR_DADOS_GEOGRAFICOS, e.getMessage()));
		} catch (IOException e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, FALHA_AO_ATUALIZAR_DADOS_GEOGRAFICOS, e.getMessage()));
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, FALHA_AO_ATUALIZAR_DADOS_GEOGRAFICOS, e.getMessage()));
		}
	}

	public boolean isIbge() {
		return ibge;
	}

	public void setIbge(boolean ibge) {
		this.ibge = ibge;
	}

	public boolean isSatelite() {
		return satelite;
	}

	public void setSatelite(boolean satelite) {
		this.satelite = satelite;
	}
}
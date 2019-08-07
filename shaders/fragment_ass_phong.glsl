
out vec4 outputColor;

uniform vec4 input_color;

uniform mat4 view_matrix;
uniform mat4 model_matrix;


// Light properties
uniform vec3 lightPos;
uniform vec3 lightIntensity;
uniform vec3 ambientIntensity;

// Material properties
uniform vec3 ambientCoeff;
uniform vec3 diffuseCoeff;
uniform vec3 specularCoeff;
uniform float phongExp;

// attenuation spotlight properties
uniform int day;
uniform float exponent;
uniform float cutoff;
uniform float outercutoff;

// fog
uniform float density;
uniform float gradient;

uniform sampler2D tex;

in vec4 viewPosition;
in vec3 m;

in vec2 texCoordFrag;


void main()
{
	if(day == 1) {
		vec3 m_unit = normalize(m);
	    // Compute the s, v and r vectors
		// Use directional light
	    vec3 s = normalize(viewPosition).xyz;
	    vec3 v = normalize(-viewPosition.xyz);
	    vec3 r = normalize(reflect(-s,m_unit));
	
	    vec3 ambient = ambientIntensity*ambientCoeff;
	    vec3 diffuse = max(lightIntensity*diffuseCoeff*dot(normalize(m_unit),s), 0.0);
	    vec3 specular = vec3(0);
	
	    vec4 ambientAndDiffuse = vec4(ambient + diffuse, 1);
	
	    outputColor = ambientAndDiffuse*input_color*texture(tex, texCoordFrag) + vec4(specular, 1);
	} else {
	
	    vec3 m_unit = normalize(m);
	    vec3 front = vec3(0, 0, 1); 
	    // Compute the s, v and r vectors
	    vec3 s = normalize(vec4(front,0)).xyz;
	    vec3 v = normalize(-viewPosition.xyz);
	    vec3 r = normalize(reflect(-s,m_unit));
		
		// Distance fog
		float distance = length(viewPosition.xyz);
		float visibility = exp(-pow((distance*density), gradient));
		visibility = clamp(visibility, 0.0 , 1.0);
		
		// spotlight
		float theta = dot(v, viewPosition.xyz);
		float epsilon = (outercutoff- cutoff);
		if(theta < epsilon) {
			
			float attenuation = pow(cos(radians(theta)), epsilon);
			
			vec3 ambient = ambientIntensity*ambientCoeff;
		    vec3 diffuse = max(attenuation*lightIntensity*diffuseCoeff*dot(normalize(m_unit),s), 0.0);
		    vec3 specular = max(lightIntensity*specularCoeff*pow(dot(r,v),phongExp), 0.0);
		    
		    vec4 ambientAndDiffuse = vec4(ambient+diffuse, 1);
		
		    outputColor = visibility*(ambientAndDiffuse*input_color*texture(tex, texCoordFrag) + vec4(specular, 1));
		} else {
			// Compute the s, v and r vectors
			// Use directional light
			s = vec3(0);
		
			vec3 ambient = ambientIntensity*ambientCoeff;
    		//vec3 diffuse = max(lightIntensity*diffuseCoeff*dot(normalize(m_unit),s), 0.0);
    		vec3 diffuse = vec3(0);// max(lightIntensity*diffuseCoeff*dot(normalize(m_unit),s), 0.0);
    		vec3 specular = vec3(0);//max(lightIntensity*pow(dot(r,v),phongExp), 0.0);;
		
		    vec4 ambientAndDiffuse = vec4(ambient + diffuse, 1);
		
		    outputColor = visibility*ambientAndDiffuse*input_color*texture(tex, texCoordFrag);
		}
	}
	
}


		//float theta = 0.5f;
		//float epsilon = outercutoff-cutoff;
		//float intensity = clamp((theta-outercutoff)/epsilon, 0.0, 1.0);
		
		

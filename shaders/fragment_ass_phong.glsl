
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
uniform float b;
uniform float exponent;
uniform float cutoff;
uniform float outercutoff;


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
	    vec3 s = normalize(view_matrix*vec4(lightPos,0) + viewPosition).xyz;
	    vec3 v = normalize(-viewPosition.xyz);
	    vec3 r = normalize(reflect(-s,m_unit));
	
	    vec3 ambient = ambientIntensity*ambientCoeff;
	    vec3 diffuse = max(lightIntensity*diffuseCoeff*dot(normalize(m_unit),s), 0.0);
	    vec3 specular = vec3(0);
	
	    vec4 ambientAndDiffuse = vec4(ambient + diffuse, 1);
	
	    outputColor = ambientAndDiffuse*input_color*texture(tex, texCoordFrag) + vec4(specular, 1);
	} else {
		vec3 m_unit = normalize(m);
	    // Compute the s, v and r vectors
		// Use directional light
	    vec3 s = normalize(view_matrix*vec4(lightPos,0)+viewPosition).xyz;
	    vec3 v = normalize(-viewPosition.xyz);
	    vec3 r = normalize(reflect(-s,m_unit));
	
	
		// spotlight
		float theta = dot(-lightPos.xyz, viewPosition.xyz);
		float epsilon = outercutoff-cutoff;
		//float intensity = clamp((theta-outercutoff)/epsilon, 0.0, 1.0);
		
		float intensity = pow(cos(radians(theta)), epsilon);
		
		// attenuation
		float distance = length(lightPos + viewPosition.xyz);
		float attenuation = 1.0f/((0.09f*distance)+(0.02f*distance*distance));
		
		
	    vec3 ambient = ambientIntensity*ambientCoeff;
	    vec3 diffuse = max(lightIntensity*diffuseCoeff*dot(normalize(m_unit),s), 0.0);
	    vec3 specular = vec3(0);
	    vec4 ambientAndDiffuse = vec4(ambient+diffuse, 1);
	
	    outputColor = attenuation*ambientAndDiffuse*input_color*texture(tex, texCoordFrag) + vec4(specular, 1);
	}
	
}

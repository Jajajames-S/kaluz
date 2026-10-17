-- Datos de prueba opcionales (Huancayo)
insert into conductores (dni,nombres,apellidos,telefono) values
 ('45678912','Luis','Quispe Rojas','964111222'),
 ('41236587','Marta','Huaman Perez','964333444')
on conflict (dni) do nothing;

insert into vehiculos (placa,tipo,marca,conductor_id,latitud,longitud,ultima_actualizacion_gps)
select 'ABC-123','TAXI','Toyota',id,-12.0651,-75.2049,now() from conductores where dni='45678912'
on conflict (placa) do nothing;
insert into vehiculos (placa,tipo,marca,conductor_id,latitud,longitud,ultima_actualizacion_gps)
select 'XYZ-789','COMBI','Hyundai',id,-12.0700,-75.2100,now() from conductores where dni='41236587'
on conflict (placa) do nothing;

insert into documentos (vehiculo_id,tipo_doc,fecha_vencimiento,estado_doc)
select id,'SOAT',current_date + 10,'POR_VENCER' from vehiculos where placa='ABC-123';
insert into documentos (vehiculo_id,tipo_doc,fecha_vencimiento,estado_doc)
select id,'REVISION_TECNICA',current_date - 5,'VENCIDO' from vehiculos where placa='XYZ-789';

from django.shortcuts import render,get_object_or_404, get_list_or_404
from .models import Building, Room, Event, EventType
from django.http import Http404
from datetime import datetime, timedelta, date
from django.views.decorators.csrf import csrf_exempt
import mysql.connector
from PIL import Image, ImageDraw
import PIL
from io import BytesIO
import requests
import urllib
import base64
import sys
sys.path.insert(0, '/home/oneadmin/mytestsite/room_displayer_project')
import settings

from datetime import timedelta
from exchangelib import CalendarItem, Account, Credentials, EWSDateTime, FolderCollection, Q, Message
from exchangelib.items import MeetingRequest,MeetingCancellation,SEND_TO_ALL_AND_SAVE_COPY


# Create your views here.
#mydb = mysql.connector.connect(host="localhost", user="room_displayer",passwd="Password!23", database="room_displayer", charset='utf8mb4')
#mycursor=mydb.cursor()
#mycursor.execute('SET NAMES utf8;') 
#mycursor.execute('SET CHARACTER SET utf8mb4;') 
#mycursor.close()
#mydb.close()

dbhost = "localhost"
dbuser = "room_displayer"
dbpwd = "Password!23"
dbdatabase = "room_displayer"
dbcharset = 'utf8mb4'

######################################################################################
######################################################################################



def index(request):
    try:
        mail = request.META['mail']
        second_name = request.META['sn']
    except:
        mail = "N\D"
        second_name = "N\D"
   
    context = {
    'mail' : mail,
    'second_name' : second_name,
	}
    return render(request, 'index.html', context=context)

######################################################################################

def departments(request):
    try:
        mail = request.META['mail']
        second_name = request.META['sn']
    except:
        mail = "N\D"
        second_name = "N\D"
    context = {
    'mail' : mail,
    'second_name' : second_name,
	}
    return render(request, 'departamentos.html', context=context)

######################################################################################

def departments_book(request):
    try:
        mail = request.META['mail']
        second_name = request.META['sn']
        name = request.META['givenName']
    except:
        mail = "N\D"
        second_name = "N\D"
    context = {
		'mail' : mail,
        'second_name' : second_name,
        'name' : name,
	}
    return render(request, 'departamentos_reserva.html', context=context)

######################################################################################

def depart_book_rooms(request, dep_id):
    try:
        mail = request.META['mail']
        second_name = request.META['sn']
    except:
        mail = "N\D"
        second_name = "N\D"

    mydb = mysql.connector.connect(host=dbhost, user=dbuser, passwd=dbpwd, database=dbdatabase, charset=dbcharset)
    mycursor=mydb.cursor()
    #depart = (11,'DETI')

    context = {'depart':dep_id, 'rooms1': [], 'rooms2': [], 'rooms3': [], 'mail' : mail,
        'second_name' : second_name, }

    slQ = "SELECT * FROM Room WHERE building_id = %(id)s"
    mycursor.execute(slQ, { 'id': dep_id })
    myresult = mycursor.fetchall()
    mycursor.close()
    mydb.close()

    for r in myresult:
        try:
            if int(r[1].split('.')[1]) == 1:
                context['rooms1'] = context['rooms1'] + [r]
            elif int(r[1].split('.')[1]) == 2:
                context['rooms2'] = context['rooms2'] + [r]
            elif int(r[1].split('.')[1]) == 3:
                context['rooms3'] = context['rooms3'] + [r] 
        except: 
            context['rooms1'] = context['rooms1'] + [r]
    
    return render(request, 'departamento_reserva_salas.html', context)

######################################################################################

def room_book_timetable(request, dep_id, room_id):
    try:
        mail = request.META['mail']
        second_name = request.META['sn']
    except:
        mail = "N\D"
        second_name = "N\D"

    mydb = mysql.connector.connect(host=dbhost, user=dbuser, passwd=dbpwd, database=dbdatabase, charset=dbcharset)
    mycursor=mydb.cursor()
    slQ = "SELECT * FROM Event WHERE classroom = %(id)s"
    mycursor.execute(slQ, { 'id': room_id })

    get_events = mycursor.fetchall()

    slQ = "SELECT * FROM Room WHERE id = %(id)s"
    mycursor.execute(slQ, { 'id': room_id })

    room_name = mycursor.fetchall()[0][1]
    mycursor.close()
    mydb.close()

    events = []
    for e in get_events:
        sd = datetime(e[5].year, e[5].month, e[5].day)
        ed = datetime(e[5].year, e[5].month, e[5].day)
        events += [(e[0], e[1], sd + e[2], ed + e[3], e[6], e[7], 11, e[9])]

    context = {
	    'events' : events,
	    'dep_id' : dep_id,
	    'room_id': room_id,
	    'room_name' : room_name,
    'mail' : mail,
        'second_name' : second_name,
	}
    return render(request, 'horario_book_c.html', context=context)

######################################################################################


######################################################################################

@csrf_exempt
def book(request):
    try:
        mail = request.META['mail']
        second_name = request.META['sn']
    except:
        mail = "N\D"
        second_name = "N\D"

    mydb = mysql.connector.connect(host=dbhost, user=dbuser, passwd=dbpwd, database=dbdatabase, charset=dbcharset)
    mycursor=mydb.cursor()
    slQ = "SELECT * FROM Building WHERE id = %(id)s"
    mycursor.execute(slQ, { 'id': request.POST.get('dep_id') })

    dep = mycursor.fetchall()


    slQ = "SELECT * FROM Room WHERE id = %(id)s"
    mycursor.execute(slQ, { 'id': request.POST.get('room_id') })

    room = mycursor.fetchall()
    mycursor.close()
    mydb.close()

    start= request.POST.get('start')
    end=request.POST.get('end')
    if start != None or end != None:
        context = {'dep' : dep, 'sala' : room, 
            'start' : (start[0:4] + '/' + start[5:7] + '/' + start[8:10] + ' - ' + start[11:13] + ':' + start[14:16]), 
            'end' : (end[0:4] + '/' + end[5:7] + '/' + end[8:10] + ' - ' + end[11:13] + ':' + end[14:16]),
            'sd' : start, 'ed' : end, 'mail' : mail,
        'second_name' : second_name,}
    else:
        context = {
        'mail' : mail,
        'second_name' : second_name,}
    return render(request, 'reserva.html', context)

######################################################################################

def salas(request, dep_id):
    try:
        mail = request.META['mail']
        second_name = request.META['sn']
    except:
        mail = "N\D"
        second_name = "N\D"

    mydb = mysql.connector.connect(host=dbhost, user=dbuser, passwd=dbpwd, database=dbdatabase, charset=dbcharset)
    mycursor=mydb.cursor()
    time = datetime.now()
    salas = {'dep' : dep_id, 'rooms1' : [], 'rooms2' : [], 'rooms3' : [], 'mail' : mail,
        'second_name' : second_name,}

    slQ = "SELECT * FROM Room WHERE building_id = %(id)s"
    mycursor.execute(slQ, { 'id': dep_id })
    myresult = mycursor.fetchall()
    mycursor.close()
    mydb.close()

    for r in myresult:
        if not 'SR' in r[1]:
            if check_room_event(r[0], time):
                fu = freeUntil(r[0], time)
            
                try:
                    if int(r[1].split('.')[1]) == 1:
                        salas['rooms1'] = salas['rooms1'] + [list(r) + [fu]]
                    elif int(r[1].split('.')[1]) == 2:
                        salas['rooms2'] = salas['rooms2'] + [list(r) + [fu]]
                    elif int(r[1].split('.')[1]) == 3:
                        salas['rooms3'] = salas['rooms3'] + [list(r) + [fu]]

                except:
                    salas['rooms1'] = salas['rooms1'] + [list(r) + [fu]]
              
    return render(request, 'salas.html', salas)

######################################################################################

def horario_v2(request, dep_id, room_id):
    try:
        mail = request.META['mail']
        second_name = request.META['sn']
    except:
        mail = "N\D"
        second_name = "N\D"

    mydb = mysql.connector.connect(host=dbhost, user=dbuser, passwd=dbpwd, database=dbdatabase, charset=dbcharset)
    mycursor=mydb.cursor()
    slQ = "SELECT * FROM Room WHERE id = %(id)s"
    mycursor.execute(slQ, { 'id': room_id })

    room = mycursor.fetchall()

    room_name = room[0][1]
	
    slQ = "SELECT * FROM Event WHERE classroom = %(id)s"
    mycursor.execute(slQ, { 'id': room_id })

    get_events = mycursor.fetchall()
    mycursor.close()
    mydb.close()

    events = []
    for e in get_events:
        sd = datetime(e[5].year, e[5].month, e[5].day)
        ed = datetime(e[5].year, e[5].month, e[5].day)
        events += [(e[0], e[1], sd + e[2], ed + e[3], e[6], e[7], 11, e[9])]

    context = {
        'events' : events,
        'dep_id': dep_id,
        'room_id': room_id,
        'room_name': room_name,
    'mail' : mail,
        'second_name' : second_name,
    }
    return render(request, 'horario_v2.html', context=context)

######################################################################################

def location(request, dep_id, room_id):
    try:
        mail = request.META['mail']
        second_name = request.META['sn']
    except:
        mail = "N\D"
        second_name = "N\D"

    mydb = mysql.connector.connect(host=dbhost, user=dbuser, passwd=dbpwd, database=dbdatabase, charset=dbcharset)
    mycursor=mydb.cursor()
    slQ = "SELECT * FROM Room WHERE id = %(id)s"
    mycursor.execute(slQ, { 'id': room_id })

    room = mycursor.fetchall()
    sala = room[0][1]
    mycursor.close()
    mydb.close()

    if sala[0] != '0':
        context = {
            'room_name': sala,
            'img': '',
        'mail' : mail,
        'second_name' : second_name,
        }
        return render(request, 'localizacao.html', context = context)

    sala_name = sala[1:]
    if sala_name[2] == '2':
        x = requests.get('http://websig.ua.pt/arcgis/rest/services/ed4/electronica/MapServer/find?searchText=' + sala_name + '&contains=true&searchFields=Porta&sr=&layers=29&layerDefs=&returnGeometry=true&maxAllowableOffset=&geometryPrecision=&dynamicLayers=&returnZ=false&returnM=false&gdbVersion=&f=pjson', stream=True)
        mapa = 'http://websig.ua.pt/ArcGIS/rest/services/ed4/electronica/MapServer/export?f=image&format=png&transparent=true&size=960%2C640&layers=show%3A29&bbox=100,100'
        x2 = requests.get('http://websig.ua.pt/ArcGIS/rest/services/ed4/electronica/MapServer/export?bbox=0&bboxSR=102161&layers=29&layerdefs=&size=960,640&imageSR=102161&format=png24&transparent=true&time=&layerTimeOptions=&f=json', stream=True)
    elif sala_name[2] == '3':
        x = requests.get('http://websig.ua.pt/arcgis/rest/services/ed4/electronica/MapServer/find?searchText=' + sala_name + '&contains=true&searchFields=Porta&sr=&layers=43&layerDefs=&returnGeometry=true&maxAllowableOffset=&geometryPrecision=&dynamicLayers=&returnZ=false&returnM=false&gdbVersion=&f=pjson', stream=True)
        mapa = 'http://websig.ua.pt/ArcGIS/rest/services/ed4/electronica/MapServer/export?f=image&format=png&transparent=true&size=960%2C640&layers=show%3A43&bbox=100,100'
        x2 = requests.get('http://websig.ua.pt/ArcGIS/rest/services/ed4/electronica/MapServer/export?bbox=0&bboxSR=102161&layers=43&layerdefs=&size=960,640&imageSR=102161&format=png24&transparent=true&time=&layerTimeOptions=&f=json', stream=True)
    elif sala_name[2] == '1':
        mapa = 'http://websig.ua.pt/ArcGIS/rest/services/ed4/electronica/MapServer/export?f=image&format=png&transparent=true&size=960%2C640&layers=show%3A14&bbox=100,100'
        x2 = requests.get('http://websig.ua.pt/ArcGIS/rest/services/ed4/electronica/MapServer/export?bbox=0&bboxSR=102161&layers=14&layerdefs=&size=960,640&imageSR=102161&format=png24&transparent=true&time=&layerTimeOptions=&f=json', stream=True)
        x = requests.get('http://websig.ua.pt/arcgis/rest/services/ed4/electronica/MapServer/find?searchText=' + sala_name + '&contains=true&searchFields=Porta&sr=&layers=14&layerDefs=&returnGeometry=true&maxAllowableOffset=&geometryPrecision=&dynamicLayers=&returnZ=false&returnM=false&gdbVersion=&f=pjson', stream=True)
    else:
        context = {
            'room_name': sala,
            'img': '',
        'mail' : mail,
        'second_name' : second_name,
        
        }
        return render(request, 'localizacao.html', context = context)
		
    x_data = x.json()
    allRings = x_data['results'][0]['geometry']['rings'][0]
    rings = ProcessaListaPontos(allRings)

    js = x2.json()
    xmin = js['extent']['xmin']
    xmax = js['extent']['xmax']
    ymin = js['extent']['ymin']
    ymax = js['extent']['ymax']
    imgW = js['width']
    imgH = js['height']

    proRings = ListaPontos(rings, xmin, xmax, ymin, ymax, imgW, imgH)

    resp = requests.get(mapa)

    img = Image.open(BytesIO(resp.content))
    img = img.transpose(PIL.Image.FLIP_TOP_BOTTOM)
    img = img.convert('RGBA')

    prevRing = proRings[0]

    for nextring in proRings:
        imgD = ImageDraw.Draw(img)
        imgD.line([(int(prevRing[0]), int(prevRing[1])), (int(nextring[0]), int(nextring[1]))], fill=(255,0,0), width=3)
        prevRing = nextring

    img = img.transpose(PIL.Image.FLIP_TOP_BOTTOM)
	
    imgBytes = BytesIO()
    img.save(imgBytes, format='png')
    img_base64 = base64.b64encode(imgBytes.getvalue())
    img.close()
	
    context = {
        'room_name': sala,
        'img': img_base64,
    'mail' : mail,
        'second_name' : second_name,
    }
	
    return render(request, 'localizacao.html', context = context)

######################################################################################

def salasSoon(request, dep_id, tD = 15):
    try:
        mail = request.META['mail']
        second_name = request.META['sn']
    except:
        mail = "N\D"
        second_name = "N\D"

    mydb = mysql.connector.connect(host=dbhost, user=dbuser, passwd=dbpwd, database=dbdatabase, charset=dbcharset)
    mycursor=mydb.cursor()
    time = datetime.now()
    timetD = datetime.now() + timedelta(minutes=tD)
    salas = {'dep': dep_id, 'rooms1' : [], 'rooms2' : [], 'rooms3' : [], 'mail' : mail,
        'second_name' : second_name,}

    slQ = "SELECT * FROM Room WHERE building_id = %(id)s"
    mycursor.execute(slQ, { 'id': dep_id })
    myresult = mycursor.fetchall()
    mycursor.close()
    mydb.close()

    salas = {'soonAvailable' : [], 'tD' : tD}

    for r in myresult:
        if not check_room_event(r[0], time):
            if check_room_event(r[0], timetD):
                salas['soonAvailable'] = salas['soonAvailable'] + [r]

    return render(request, 'salas_soon.html', salas)

######################################################################################

@csrf_exempt
def method(request):
    try:
        mail = request.META['mail']
        second_name = request.META['sn']

    except:
        mail = "N\D"
        second_name = "N\D"
    mydb = mysql.connector.connect(host=dbhost, user=dbuser, passwd=dbpwd, database=dbdatabase, charset=dbcharset)
    mycursor=mydb.cursor()

    dep = request.POST['dep_id']

    room = request.POST['room_id']

    sd = request.POST['start']
    ed = request.POST['end']
    p_mail = request.POST['p_mail']

    x = p_mail.split("-")
    x.pop()
    x.append(mail)

    sd_year = int(sd[0:4])
    sd_month = int(sd[5:7])
    sd_day = int(sd[8:10])
    sd_hour = int(sd[11:13]) - 1
    sd_minute = int(sd[14:16])

    ed_year = int(ed[0:4])
    ed_month = int(ed[5:7])
    ed_day = int(ed[8:10])
    ed_hour = int(ed[11:13]) - 1
    ed_minute = int(ed[14:16])

    time = datetime.now()

    if (int(ed[8:10]) != int(sd[8:10])) or ((int(ed[11:13]) - int(sd[11:13])) > 2) or ((int(ed[11:13]) - int(sd[11:13])) == 2 and (int(ed[14:16]) - int(sd[14:16]) == 30)) or ((int(sd[8:10]) < int(time.strftime("%d"))) and (int(sd[5:7]) == int(time.strftime("%m")))) or (int(sd[5:7]) < int(time.strftime("%m"))) or ((int(sd[8:10])) == int(time.strftime("%d")) and (int(sd[11:13]) < (int(time.strftime("%H")) + 1))) or ((int(sd[8:10])) == int(time.strftime("%d")) and (int(sd[11:13]) == (int(time.strftime("%H")) + 1)) and (int(sd[14:16]) < (int(time.strftime("%M"))) )):
        slQ = "SELECT * FROM Building WHERE id = %(id)s"
        mycursor.execute(slQ, { 'id': request.POST.get('dep_id') })

        dep = mycursor.fetchall()

        slQ = "SELECT * FROM Room WHERE id = %(id)s"
        mycursor.execute(slQ, { 'id': request.POST.get('room_id') })

        room = mycursor.fetchall()
        mycursor.close()
        mydb.close()

        context = {'dep' : dep, 'sala' : room, 'start' : '', 'end' : '', 'sd' : '', 'ed' : '', 'mail' : mail,
        'second_name' : second_name,}
        return render(request, 'reserva.html', context=context)

    type = 6

    name = 'Reserva'

    participants = 1
    slQ = "SELECT * FROM Event"
    mycursor.execute(slQ)

    get_events = mycursor.fetchall()
    if get_events != []:
        id = get_events[-1][0] + 1
    else:
        id = 1

    day = datetime.strptime(sd[0:4] + sd[5:7] + sd[8:10], "%Y%m%d").date()
    sdTime = datetime.strptime(sd[11:13] + ':' + sd[14:16] + ":00","%H:%M:%S")
    edTime = datetime.strptime(ed[11:13] + ':' + ed[14:16] + ":00","%H:%M:%S")
    duration = edTime - sdTime

    insert_stmt = (
        "INSERT INTO Event (id, name, startTime, endTime, duration , day, eventType, numberPeople , isOwner, classroom) "
        "VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s)"
    )
    data = (id, name, sdTime, edTime, duration, day, type, participants, 0, room)
    mycursor.execute(insert_stmt, data)
    mydb.commit()

    slQ = "SELECT * FROM Event WHERE classroom = %(id)s"
    mycursor.execute(slQ, { 'id': room })

    get_events = mycursor.fetchall()

    slQ = "SELECT * FROM Room WHERE id = %(id)s"
    mycursor.execute(slQ, { 'id': room })

    room = mycursor.fetchall()
    sala = room[0][1]

    mycursor.close()
    mydb.close()

    events = []
    for e in get_events:
        sd = datetime(e[5].year, e[5].month, e[5].day)
        ed = datetime(e[5].year, e[5].month, e[5].day)
        events += [(e[0], e[1], sd + e[2], ed + e[3], e[6], e[7], 11, e[9])]

    context = {
	    'events' : events,
	    'dep_id' : dep,
	    'room_name': sala,
    'mail' : mail,
        'second_name' : second_name,
	}

    #Exchange

    credentials = Credentials('detiroom@outlook.com', "2020roomdeti")
    a = Account('detiroom@outlook.com', credentials=credentials, autodiscover=True)  
 

    for email in x:
        if email != "":
            if sd_minute == 0 and ed_minute == 0: 
                item = CalendarItem(
                    account = a,
                    folder = a.calendar,
                    start = a.default_timezone.localize(EWSDateTime(sd_year,sd_month,sd_day,sd_hour,00)),
                    end = a.default_timezone.localize(EWSDateTime(ed_year,ed_month,ed_day,ed_hour,00)),
                    subject = "Reserva de sala via (DetiRoom Web)",
                    body = "Foi convidado para o evento na sala "+sala+" do departamento 4, das "+str(sd_hour+1)+":00 ás " +str(ed_hour+1)+":00",
                    required_attendees = [email]
                )
                
                item.save(send_meeting_invitations=SEND_TO_ALL_AND_SAVE_COPY)
            elif sd_minute == 0 and ed_minute != 0:
                item = CalendarItem(
                    account = a,
                    folder = a.calendar,
                    start = a.default_timezone.localize(EWSDateTime(sd_year,sd_month,sd_day,sd_hour,00)),
                    end = a.default_timezone.localize(EWSDateTime(ed_year,ed_month,ed_day,ed_hour,ed_minute)),
                    subject = "Reserva de sala via (DetiRoom Web)",
                    body = "Foi convidado para o evento na sala "+sala+" do departamento 4, das "+str(sd_hour+1)+":00 ás " +str(ed_hour+1)+":"+str(ed_minute),
                    required_attendees = [email]
                )
                
                item.save(send_meeting_invitations=SEND_TO_ALL_AND_SAVE_COPY)
            elif sd_minute != 0 and ed_minute == 0:
                item = CalendarItem(
                    account = a,
                    folder = a.calendar,
                    start = a.default_timezone.localize(EWSDateTime(sd_year,sd_month,sd_day,sd_hour,sd_minute)),
                    end = a.default_timezone.localize(EWSDateTime(ed_year,ed_month,ed_day,ed_hour,00)),
                    subject = "Reserva de sala via (DetiRoom Web)",
                    body = "Foi convidado para o evento na sala "+sala+" do departamento 4, das "+str(sd_hour+1)+":"+str(sd_minute)+" ás " +str(ed_hour+1)+":00",
                    required_attendees = [email]
                )
                
                item.save(send_meeting_invitations=SEND_TO_ALL_AND_SAVE_COPY)
            else:
                item = CalendarItem(
                    account = a,
                    folder = a.calendar,
                    start = a.default_timezone.localize(EWSDateTime(sd_year,sd_month,sd_day,sd_hour,sd_minute)),
                    end = a.default_timezone.localize(EWSDateTime(ed_year,ed_month,ed_day,ed_hour,ed_minute)),
                    subject = "Reserva de sala via (DetiRoom Web)",
                    body = "Foi convidado para o evento na sala "+sala+" do departamento 4, das "+str(sd_hour+1)+":"+str(sd_minute)+" ás " +str(ed_hour+1)+":"+str(ed_minute),
                    required_attendees = [email]
                )
                
                item.save(send_meeting_invitations=SEND_TO_ALL_AND_SAVE_COPY)

    return render(request, 'horario_book_c.html', context=context)

######################################################################################
######################################################################################

# FUNCOES AUXILIARES																 

######################################################################################

def check_room_event(rid, time):
    mydb = mysql.connector.connect(host=dbhost, user=dbuser, passwd=dbpwd, database=dbdatabase, charset=dbcharset)
    mycursor=mydb.cursor()
    slQ = "SELECT * FROM Event WHERE classroom = %(id)s"
    mycursor.execute(slQ, { 'id': rid })

    events = mycursor.fetchall()
    mycursor.close()
    mydb.close()

    hora = int(time.strftime("%H")) + 1

    if len(events) == 0:
        return True

    for e in events:
        sd = datetime(e[5].year, e[5].month, e[5].day) + e[2]
        ed = datetime(e[5].year, e[5].month, e[5].day) + e[3]
        if (int(sd.strftime("%d")) == int(time.strftime("%d"))) and (int(sd.strftime("%m")) == int(time.strftime("%m"))) and (int(sd.strftime("%Y")) == int(time.strftime("%Y"))):
            if int(sd.strftime("%H")) <= hora:
                if (int(sd.strftime("%H")) == hora) and (int(sd.strftime("%M")) <= int(time.strftime("%M"))):
                    if (int(ed.strftime("%H")) > hora) or (int(ed.strftime("%M")) > int(time.strftime("%M")) and (int(ed.strftime("%H")) == hora)):
                        return False
                elif int(ed.strftime("%H")) > hora:
                    return False
                elif int(ed.strftime("%H")) == hora:
                    if int(ed.strftime("%M")) > int(time.strftime("%M")):
                        return False

    return True

######################################################################################

def freeUntil(rid, time):
    mydb = mysql.connector.connect(host=dbhost, user=dbuser, passwd=dbpwd, database=dbdatabase, charset=dbcharset)
    mycursor=mydb.cursor()
    slQ = "SELECT * FROM Event WHERE classroom = %(id)s"
    mycursor.execute(slQ, { 'id': rid })

    events = mycursor.fetchall()
    mycursor.close()
    mydb.close()

    hora = int(time.strftime("%H")) + 1
    soon = [20, 0]
    if len(events) == 0:
        return "20:00"

    for e in events:
        sd = datetime(e[5].year, e[5].month, e[5].day) + e[2]
        ed = datetime(e[5].year, e[5].month, e[5].day) + e[3]
        if (int(sd.strftime("%d")) == int(time.strftime("%d"))) and (int(sd.strftime("%m")) == int(time.strftime("%m"))) and (int(sd.strftime("%Y")) == int(time.strftime("%Y"))):
            if int(sd.strftime("%H")) > hora:
                if int(sd.strftime("%H")) < soon[0]:
                    soon[0] = int(sd.strftime("%H"))
                    soon[1] = int(sd.strftime("%M"))
                elif int(sd.strftime("%H")) == soon[0]:
                    if int(sd.strftime("%M")) < soon[1]:
                        soon[1] = int(sd.strftime("%M"))

    if soon[1] == 0:
        return str(soon[0]) + ":" + str(soon[1]) + "0"
    else:
        return str(soon[0]) + ":" + str(soon[1])
		
#########################################################################################
		
def ListaPontos(lista_pontosf, xmin, xmax, ymin, ymax, imgW, imgH):
    res_pontos = []
    c1 = (xmax - xmin) / imgW
    c2 = (ymax - ymin) / imgH
    
    for lf in lista_pontosf:
        x = int((lf[0] - xmin) / c1)
        y = int((lf[1] - ymin) / c2)
        res_pontos += [[x,y]]

    return res_pontos;

#########################################################################################

def ProcessaListaPontos(pontos_raw):
    res1 = []
    for pontos_raw2 in pontos_raw:
        res1 += [[float(pontos_raw2[0]), float(pontos_raw2[1])]]
        
    return res1;

##########################################################################################